package com.blamejared.controlling.client.gui;

import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.StatCollector;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

@SideOnly(Side.CLIENT)
public class GuiNewControls extends GuiControls {
    private static final GameSettings.Options[] OPTIONS_ARR = new GameSettings.Options[]{
        GameSettings.Options.INVERT_MOUSE,
        GameSettings.Options.SENSITIVITY,
        GameSettings.Options.TOUCHSCREEN
    };

    private final GuiScreen parentScreen;
    private final GameSettings options;
    private GuiButton buttonReset;
    private String lastSearch;
    private GuiTextField search;

    private DisplayMode displayMode;
    private SearchType searchType;
    private SortOrder sortOrder;

    private GuiButton buttonNone;
    private GuiButton buttonConflicting;
    private GuiCheckBox buttonKey;
    private GuiCheckBox buttonCat;
    private GuiButton sortOrderButton;
    private boolean confirmingReset = false;

    public GuiNewControls(GuiScreen screen, GameSettings settings) {
        super(screen, settings);
        this.parentScreen = screen;
        this.options = settings;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    @Override
    public void initGui() {
        this.screenTitle = StatCollector.translateToLocal("controls.title");
        int i = 0;

        for (GameSettings.Options gameOption : OPTIONS_ARR) {
            if (gameOption.getEnumFloat()) {
                this.buttonList.add(new GuiOptionSlider(
                    gameOption.returnEnumOrdinal(),
                    this.width / 2 - 155 + i % 2 * 160,
                    18 + 24 * (i >> 1),
                    gameOption
                ));
            } else {
                this.buttonList.add(new GuiOptionButton(
                    gameOption.returnEnumOrdinal(),
                    this.width / 2 - 155 + i % 2 * 160,
                    18 + 24 * (i >> 1),
                    gameOption,
                    this.options.getKeyBinding(gameOption)
                ));
            }
            ++i;
        }

        this.keyBindingList = new GuiNewKeyBindingList(this, this.mc);

        this.buttonList.add(new GuiButton(
            1001,
            this.width / 2 - 155 + 160,
            this.height - 29,
            150,
            20,
            StatCollector.translateToLocal("gui.done")
        ));

        this.buttonReset = new GuiButton(
            1002,
            this.width / 2 - 155,
            this.height - 29,
            150,
            20,
            StatCollector.translateToLocal("controls.resetAll")
        );
        this.buttonList.add(this.buttonReset);

        this.buttonNone = new GuiButton(
            1003,
            this.width / 2 - 155 + 160 + 76,
            this.height - 29 - 24,
            150 / 2,
            20,
            StatCollector.translateToLocal("options.showNone")
        );
        this.buttonList.add(this.buttonNone);

        this.buttonConflicting = new GuiButton(
            1004,
            this.width / 2 - 155 + 160,
            this.height - 29 - 24,
            150 / 2,
            20,
            StatCollector.translateToLocal("options.showConflicts")
        );
        this.buttonList.add(this.buttonConflicting);

        this.search = new GuiTextField(fontRendererObj, this.width / 2 - 154, this.height - 29 - 23, 148, 18);
        search.setCanLoseFocus(true);

        this.buttonKey = new GuiCheckBox(
            1005,
            this.width / 2 - (155 / 2),
            this.height - 29 - 37,
            StatCollector.translateToLocal("options.key"),
            false
        );
        this.buttonList.add(this.buttonKey);

        this.buttonCat = new GuiCheckBox(
            1006,
            this.width / 2 - (155 / 2),
            this.height - 29 - 50,
            StatCollector.translateToLocal("options.category"),
            false
        );
        this.buttonList.add(this.buttonCat);

        this.sortOrderButton = new GuiButton(
            1008,
            this.width / 2 - 155 + 160 + 76,
            this.height - 29 - 24 - 24,
            150 / 2,
            20,
            StatCollector.translateToLocal("options.sort")
        );
        this.buttonList.add(this.sortOrderButton);
        this.sortOrder = SortOrder.NONE;
        this.lastSearch = "";
        this.displayMode = DisplayMode.ALL;
        this.searchType = SearchType.NAME;
    }

    @Override
    public void updateScreen() {
        this.search.updateCursorCounter();
        if (!lastSearch.equals(search.getText())) {
            filterKeys();
        }
    }

    public void filterKeys() {
        lastSearch = search.getText();
        if (lastSearch.isEmpty() && displayMode == DisplayMode.ALL && sortOrder == SortOrder.NONE && searchType != SearchType.NAME) {
            return;
        }

        this.keyBindingList.scrollBy(-this.keyBindingList.getAmountScrolled());
        Predicate<GuiNewKeyBindingList.KeyEntry> filters = displayMode.getPredicate();

        switch (searchType) {
            case NAME:
                filters = filters.and(keyEntry -> keyEntry.getKeyDesc()
                    .toLowerCase()
                    .contains(lastSearch.toLowerCase()));
                break;
            case CATEGORY:
                filters = filters.and(keyEntry -> StatCollector.translateToLocal(keyEntry.getKeybinding().getKeyCategory())
                    .toLowerCase()
                    .contains(lastSearch.toLowerCase()));
                break;
            case KEY:
                filters = filters.and(keyEntry -> GameSettings.getKeyDisplayString(keyEntry.getKeybinding()
                                                                                       .getKeyCode())
                    .toLowerCase()
                    .contains(lastSearch.toLowerCase()));
                break;
        }

        LinkedList<GuiListExtended.IGuiListEntry> workingList = new LinkedList<>();

        for (GuiListExtended.IGuiListEntry entry : ((GuiNewKeyBindingList) keyBindingList).getAllEntries()) {
            if (searchType == SearchType.CATEGORY && sortOrder == SortOrder.NONE && displayMode == DisplayMode.ALL) {
                if (entry instanceof GuiNewKeyBindingList.KeyEntry) {
                    GuiNewKeyBindingList.KeyEntry keyEntry = (GuiNewKeyBindingList.KeyEntry) entry;
                    if (filters.test(keyEntry)) {
                        workingList.add(entry);
                    }
                } else {
                    workingList.add(entry);
                }
            } else {
                if (entry instanceof GuiNewKeyBindingList.KeyEntry) {
                    GuiNewKeyBindingList.KeyEntry keyEntry = (GuiNewKeyBindingList.KeyEntry) entry;
                    if (filters.test(keyEntry)) {
                        workingList.add(entry);
                    }
                }
            }
        }

        if (searchType == SearchType.CATEGORY && sortOrder == SortOrder.NONE && displayMode == DisplayMode.ALL) {
            Set<GuiNewKeyBindingList.CategoryEntry> categories = new LinkedHashSet<>();

            for (GuiListExtended.IGuiListEntry entry : workingList) {
                if (entry instanceof GuiNewKeyBindingList.CategoryEntry) {
                    GuiNewKeyBindingList.CategoryEntry categoryEntry = (GuiNewKeyBindingList.CategoryEntry) entry;
                    categories.add(categoryEntry);
                    for (GuiListExtended.IGuiListEntry child : workingList) {
                        if (child instanceof GuiNewKeyBindingList.KeyEntry) {
                            GuiNewKeyBindingList.KeyEntry childEntry = (GuiNewKeyBindingList.KeyEntry) child;
                            if (childEntry.getKeybinding().getKeyCategory().equals(categoryEntry.getName())) {
                                categories.remove(categoryEntry);
                            }
                        }
                    }
                }
            }

            workingList.removeAll(categories);
        }
        sortOrder.sort(workingList);
        ((GuiNewKeyBindingList) keyBindingList).setListEntries(workingList);
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.keyBindingList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 8, 16777215);
        boolean flag = false;

        for (KeyBinding keybinding : this.options.keyBindings) {
            if (keybinding.getKeyCode() != keybinding.getKeyCodeDefault()) {
                flag = true;
                break;
            }
        }

        search.drawTextBox();
        this.buttonReset.enabled = flag;

        if (!flag) {
            confirmingReset = false;
            buttonReset.displayString = StatCollector.translateToLocal("controls.resetAll");
        }

        for (GuiButton guiButton : (List<GuiButton>)this.buttonList) {
            guiButton.drawButton(mc, mouseX, mouseY);
        }

        String text = StatCollector.translateToLocal("options.search");
        drawCenteredString(
            fontRendererObj,
            text,
            this.width / 2 - (155 / 2) - (fontRendererObj.getStringWidth(text)) - 5,
            this.height - 29 - 42,
            16777215
        );
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id < 100 && button instanceof GuiOptionButton) {
            this.options.setOptionValue(((GuiOptionButton) button).returnEnumOptions(), 1);
            button.displayString = this.options.getKeyBinding(GameSettings.Options.getEnumOptions(button.id));
        } else if (button.id == 1001) {
            mc.displayGuiScreen(this.parentScreen);
        } else if (button.id == 1002) {
            if (!confirmingReset) {
                confirmingReset = true;
                button.displayString = StatCollector.translateToLocal("options.confirmReset");
                return;
            }

            confirmingReset = false;
            button.displayString = StatCollector.translateToLocal("controls.resetAll");

            for (KeyBinding keyBinding : mc.gameSettings.keyBindings) {
                keyBinding.setKeyCode(keyBinding.getKeyCodeDefault());
            }
            KeyBinding.resetKeyBindingArrayAndHash();
        } else if (button.id == 1003) {
            if (displayMode == DisplayMode.NONE) {
                buttonNone.displayString = StatCollector.translateToLocal("options.showNone");
                displayMode = DisplayMode.ALL;
            } else {
                displayMode = DisplayMode.NONE;
                buttonNone.displayString = StatCollector.translateToLocal("options.showAll");
                buttonConflicting.displayString = StatCollector.translateToLocal("options.showConflicts");
            }
            filterKeys();
        } else if (button.id == 1004) {
            if (displayMode == DisplayMode.CONFLICTING) {
                buttonConflicting.displayString = StatCollector.translateToLocal("options.showConflicts");
                displayMode = DisplayMode.ALL;
            } else {
                displayMode = DisplayMode.CONFLICTING;
                buttonConflicting.displayString = StatCollector.translateToLocal("options.showAll");
                buttonNone.displayString = StatCollector.translateToLocal("options.showNone");
            }
            filterKeys();
        } else if (button.id == 1005) {
            buttonCat.setIsChecked(false);
            searchType = buttonKey.isChecked() ? SearchType.KEY : SearchType.NAME;
            filterKeys();
        } else if (button.id == 1006) {
            buttonKey.setIsChecked(false);
            searchType = buttonCat.isChecked() ? SearchType.CATEGORY : SearchType.NAME;
            filterKeys();
        }else if (button.id == 1008) {
            sortOrder = sortOrder.cycle();
            button.displayString = StatCollector.translateToLocal("options.sort") + ": " + sortOrder.getName();
            filterKeys();
        }
    }

    @Override
    public void mouseClicked(int mx, int my, int mb) {
        if (this.buttonId != null) {
            this.options.setOptionKeyBinding(this.buttonId, -100 + mb);
            this.buttonId = null;
            KeyBinding.resetKeyBindingArrayAndHash();
            search.setFocused(false);
        } else if (mb == 0 && !this.keyBindingList.func_148179_a(mx, my, mb)) { // func_148179_a is mouseClicked but still obfuscated in 1.7.10
            try {
                superSuperMouseClicked(mx, my, mb);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        search.mouseClicked(mx, my, mb);
        if (search.isFocused() && mb == 1) {
            search.setText("");
        }
    }

    protected void superSuperMouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            for (int i = 0; i < this.buttonList.size(); ++i) {
                GuiButton guibutton = (GuiButton) this.buttonList.get(i);

                if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
                    net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(
                        this,
                        guibutton,
                        this.buttonList
                    );

                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                        break;

                    guibutton = event.button;
                    this.selectedButton = guibutton;
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);

                    if (this.equals(this.mc.currentScreen)) {
                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(
                            this,
                            event.button,
                            this.buttonList
                        ));
                    }
                }
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state != 0 || !this.keyBindingList.func_148181_b(mouseX, mouseY, state)) { // func_148181_b is mouseReleased but still obfuscated in 1.7.10
            superSuperMouseReleased(mouseX, mouseY, state);
        }
    }

    protected void superSuperMouseReleased(int mouseX, int mouseY, int state) {
        if (this.selectedButton != null && state == 0) {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.buttonId != null) {
            if (keyCode == 1) {
                this.options.setOptionKeyBinding(this.buttonId, 0);
            } else if (keyCode != 0) {
                this.options.setOptionKeyBinding(this.buttonId, keyCode);
            } else if (typedChar > 0) {
                this.options.setOptionKeyBinding(this.buttonId, typedChar + 256);
            }

            this.buttonId = null;
            this.time = Minecraft.getSystemTime();
            KeyBinding.resetKeyBindingArrayAndHash();
        } else {
            if (search.isFocused())
                search.textboxKeyTyped(typedChar, keyCode);
            else {
                superSuperKeyTyped(typedChar, keyCode);
            }
        }
    }

    protected void superSuperKeyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);

            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
        }
    }
}
