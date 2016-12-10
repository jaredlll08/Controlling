package us.getfluxed.controlsearch.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.*;

import java.io.IOException;
import java.util.LinkedList;

public class GuiNewControls extends GuiScreen {
	
	private static final GameSettings.Options[] OPTIONS_ARR = new GameSettings.Options[]{GameSettings.Options.INVERT_MOUSE, GameSettings.Options.SENSITIVITY, GameSettings.Options.TOUCHSCREEN, GameSettings.Options.AUTO_JUMP};
	/**
	 * A reference to the screen object that created this. Used for navigating between screens.
	 */
	private final GuiScreen parentScreen;
	protected String screenTitle = "Controls";
	/**
	 * Reference to the GameSettings object.
	 */
	private final GameSettings options;
	/**
	 * The ID of the button that has been pressed.
	 */
	public KeyBinding buttonId;
	public long time;
	public GuiNewKeyBindingList keyBindingList;
	private GuiButton buttonReset;
	
	private GuiTextField search;
	private String lastFilterText = "";
	
	private boolean conflicts = false;
	
	public GuiNewControls(GuiScreen screen, GameSettings settings) {
		this.parentScreen = screen;
		this.options = settings;
	}
	
	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
	 * window resizes, the buttonList is cleared beforehand.
	 */
	public void initGui() {
		this.keyBindingList = new GuiNewKeyBindingList(this, this.mc);
		this.buttonList.add(new GuiButton(200, this.width / 2 - 155, this.height - 29, 150, 20, I18n.format("gui.done", new Object[0])));
		this.buttonReset = this.func_189646_b(new GuiButton(201, this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.format("controls.resetAll", new Object[0])));
		this.screenTitle = I18n.format("controls.title", new Object[0]);
		int i = 0;
		
		for(GameSettings.Options gamesettings$options : OPTIONS_ARR) {
			if(gamesettings$options.getEnumFloat()) {
				this.buttonList.add(new GuiOptionSlider(gamesettings$options.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), gamesettings$options));
			} else {
				this.buttonList.add(new GuiOptionButton(gamesettings$options.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, 18 + 24 * (i >> 1), gamesettings$options, this.options.getKeyBinding(gamesettings$options)));
			}
			
			++i;
		}
		
		search = new GuiTextField(0, mc.fontRendererObj, this.width / 2 - 155, this.height - 29 - 28, 150, 18);
		search.setFocused(true);
		search.setCanLoseFocus(true);
		this.buttonList.add(new GuiButton(2906, this.width / 2 - 155 + 160, this.height - 29 - 29, 150, 20, I18n.format("options.showConflicts")));
		
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		search.updateCursorCounter();
		if(!search.getText().equals(lastFilterText)) {
			reloadKeys(0);
		}
	}
	
	private void reloadKeys(int type) {
		if(type == 0) {
			LinkedList<GuiListExtended.IGuiListEntry> newList = new LinkedList<>();
			for(GuiListExtended.IGuiListEntry entry : keyBindingList.getListEntriesAll()) {
				if(entry instanceof GuiNewKeyBindingList.KeyEntry) {
					GuiNewKeyBindingList.KeyEntry ent = (GuiNewKeyBindingList.KeyEntry) entry;
					if(ent.getKeybinding().getKeyDescription().toLowerCase().contains(search.getText().toLowerCase())) {
						newList.add(entry);
					}
				}
			}
			keyBindingList.setListEntries(newList);
			lastFilterText = search.getText();
			if(lastFilterText.isEmpty()) {
				keyBindingList.setListEntries(keyBindingList.getListEntriesAll());
			}
		} else if(type == 1) {
			LinkedList<GuiListExtended.IGuiListEntry> conflicts = new LinkedList<>();
			for(GuiListExtended.IGuiListEntry entry : keyBindingList.getListEntriesAll()) {
				if(entry instanceof GuiNewKeyBindingList.KeyEntry) {
					GuiNewKeyBindingList.KeyEntry ent = (GuiNewKeyBindingList.KeyEntry) entry;
					for(GuiListExtended.IGuiListEntry entry1 : keyBindingList.getListEntriesAll()) {
						if(!entry.equals(entry1))
							if(entry1 instanceof GuiNewKeyBindingList.KeyEntry) {
								GuiNewKeyBindingList.KeyEntry ent1 = (GuiNewKeyBindingList.KeyEntry) entry1;
								if(ent.getKeybinding().conflicts(ent1.getKeybinding())) {
									if(!conflicts.contains(ent))
										conflicts.add(ent);
									if(!conflicts.contains(ent1))
										conflicts.add(ent1);
								}
							}
					}
					
				}
			}
			keyBindingList.setListEntries(conflicts);
			if(conflicts.isEmpty() || !this.conflicts) {
				keyBindingList.setListEntries(keyBindingList.getListEntriesAll());
			}
		}
	}
	
	/**
	 * Handles mouse input.
	 */
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		this.keyBindingList.handleMouseInput();
	}
	
	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button.id == 200) {
			this.mc.displayGuiScreen(this.parentScreen);
		} else if(button.id == 201) {
			for(KeyBinding keybinding : this.mc.gameSettings.keyBindings) {
				keybinding.setToDefault();
			}
			
			KeyBinding.resetKeyBindingArrayAndHash();
		} else if(button.id < 100 && button instanceof GuiOptionButton) {
			this.options.setOptionValue(((GuiOptionButton) button).returnEnumOptions(), 1);
			button.displayString = this.options.getKeyBinding(GameSettings.Options.getEnumOptions(button.id));
		} else if(button.id == 2906) {
			if(!conflicts) {
				conflicts = true;
				reloadKeys(1);
			} else {
				conflicts = false;
				reloadKeys(1);
			}
			
			
		}
	}
	
	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(this.buttonId != null) {
			this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), -100 + mouseButton);
			this.options.setOptionKeyBinding(this.buttonId, -100 + mouseButton);
			this.buttonId = null;
			KeyBinding.resetKeyBindingArrayAndHash();
		} else if(mouseButton != 0 || !this.keyBindingList.mouseClicked(mouseX, mouseY, mouseButton)) {
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
		search.mouseClicked(mouseX, mouseY, mouseButton);
		if(mouseButton == 1 && mouseX >= search.xPosition && mouseX < search.xPosition + search.width && mouseY >= search.yPosition && mouseY < search.yPosition + search.height) {
			search.setText("");
		}
	}
	
	/**
	 * Called when a mouse button is released.
	 */
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if(state != 0 || !this.keyBindingList.mouseReleased(mouseX, mouseY, state)) {
			super.mouseReleased(mouseX, mouseY, state);
		}
	}
	
	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(this.buttonId != null) {
			if(keyCode == 1) {
				this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.NONE, 0);
				this.options.setOptionKeyBinding(this.buttonId, 0);
			} else if(keyCode != 0) {
				this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), keyCode);
				this.options.setOptionKeyBinding(this.buttonId, keyCode);
			} else if(typedChar > 0) {
				this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), typedChar + 256);
				this.options.setOptionKeyBinding(this.buttonId, typedChar + 256);
			}
			//This is the modifier section, to the game, it appears that shift is down when it is not, this is the fix.
//			if(!net.minecraftforge.client.settings.KeyModifier.isKeyCodeModifier(keyCode))
				this.buttonId = null;
			this.time = Minecraft.getSystemTime();
			KeyBinding.resetKeyBindingArrayAndHash();
		} else {
			if(search.isFocused())
				search.textboxKeyTyped(typedChar, keyCode);
			else
				super.keyTyped(typedChar, keyCode);
		}
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.keyBindingList.drawScreen(mouseX, mouseY, partialTicks);
		this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 8, 16777215);
		this.drawCenteredString(this.fontRendererObj, I18n.format("options.search"), this.width / 2 - (155 / 2), this.height - 29 - 44, 16777215);
		boolean flag = false;
		
		for(KeyBinding keybinding : this.options.keyBindings) {
			if(!keybinding.isSetToDefaultValue()) {
				flag = true;
				break;
			}
		}
		
		this.buttonReset.enabled = flag;
		super.drawScreen(mouseX, mouseY, partialTicks);
		search.drawTextBox();
	}
}
