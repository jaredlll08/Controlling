package com.blamejared.controlling.client.gui;

import com.blamejared.controlling.Controlling;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.screen.MouseSettingsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.KeyBindingList;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class GuiNewControls extends ControlsScreen {

    private Button buttonReset;
    private final Screen parentScreen;
    private final GameSettings options;

    private String lastSearch;
    private TextFieldWidget search;

    private DisplayMode displayMode;
    private SearchType searchType;
    private SortOrder sortOrder;

    private Button buttonNone;
    private Button buttonConflicting;
    private GuiCheckBox buttonKey;
    private GuiCheckBox buttonCat;
    private Button patreonButton;
    private boolean confirmingReset = false;

    private String name;

    public GuiNewControls(Screen screen, GameSettings settings) {
        super(screen, settings);
        this.parentScreen = screen;
        this.options = settings;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    protected void init() {

        this.addButton(new Button(this.width / 2 - 155, 18, 150, 20, new TranslationTextComponent("options.mouse_settings"), (p_213126_1_) -> {
            this.minecraft.displayGuiScreen(new MouseSettingsScreen(this, options));
        }));
        this.addButton(AbstractOption.AUTO_JUMP.createWidget(this.minecraft.gameSettings, this.width / 2 - 155 + 160, 18, 150));

        this.keyBindingList = new GuiNewKeyBindingList(this, this.minecraft);
        this.children.add(this.keyBindingList);
        this.setListener(this.keyBindingList);
        this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, new TranslationTextComponent("gui.done"), (p_213126_1_) -> GuiNewControls.this.minecraft.displayGuiScreen(GuiNewControls.this.parentScreen)));

        this.buttonReset = this.addButton(new Button(this.width / 2 - 155, this.height - 29, 150, 20, new TranslationTextComponent("controls.resetAll"), (p_213126_1_) -> {

            if (!confirmingReset) {
                confirmingReset = true;
                p_213126_1_.setMessage(new TranslationTextComponent("options.confirmReset"));
                return;
            }
            confirmingReset = false;
            p_213126_1_.setMessage(new TranslationTextComponent("controls.resetAll"));
            for (KeyBinding keybinding : GuiNewControls.this.minecraft.gameSettings.keyBindings) {
                keybinding.setToDefault();
            }

            KeyBinding.resetKeyBindingArrayAndHash();
        }));
        this.buttonNone = this.addButton(new Button(this.width / 2 - 155 + 160 + 76, this.height - 29 - 24, 150 / 2, 20, new TranslationTextComponent("options.showNone"), (p_213126_1_) -> {
            if (displayMode == DisplayMode.NONE) {
                buttonNone.setMessage(new TranslationTextComponent("options.showNone"));
                displayMode = DisplayMode.ALL;
            } else {
                displayMode = DisplayMode.NONE;
                buttonNone.setMessage(new TranslationTextComponent("options.showAll"));
                buttonConflicting.setMessage(new TranslationTextComponent("options.showConflicts"));
            }
            filterKeys();
        }));
        this.buttonConflicting = this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29 - 24, 150 / 2, 20, new TranslationTextComponent("options.showConflicts"), (p_213126_1_) -> {
            if (displayMode == DisplayMode.CONFLICTING) {
                buttonConflicting.setMessage(new TranslationTextComponent("options.showConflicts"));
                displayMode = DisplayMode.ALL;
            } else {
                displayMode = DisplayMode.CONFLICTING;
                buttonConflicting.setMessage(new TranslationTextComponent("options.showAll"));
                buttonNone.setMessage(new TranslationTextComponent("options.showNone"));
            }
            filterKeys();
        }));
        search = new TextFieldWidget(font, this.width / 2 - 154, this.height - 29 - 23, 148, 18, new StringTextComponent(""));
        this.buttonKey = this.addButton(new GuiCheckBox(this.width / 2 - (155 / 2), this.height - 29 - 37, new TranslationTextComponent("options.key").getString(), false) {
            @Override
            public void onPress() {
                super.onPress();
                buttonCat.setIsChecked(false);
                searchType = this.isChecked() ? SearchType.KEY : SearchType.NAME;
                filterKeys();
            }
        });
        this.buttonCat = this.addButton(new GuiCheckBox(this.width / 2 - (155 / 2), this.height - 29 - 50, new TranslationTextComponent("options.category").getString(), false) {

            @Override
            public void onPress() {
                super.onPress();
                buttonKey.setIsChecked(false);
                searchType = this.isChecked() ? SearchType.CATEGORY : SearchType.NAME;
                filterKeys();
            }
        });
        name = Controlling.PATRON_LIST.stream().skip(Controlling.PATRON_LIST.isEmpty() ? 0 : new Random().nextInt(Controlling.PATRON_LIST.size())).findFirst().orElse("");
        patreonButton = this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29 - 24 - 24, 150 / 2, 20, new StringTextComponent("Patreon"), p_onPress_1_ -> {
            Util.getOSType().openURI("https://patreon.com/jaredlll08?s=controllingmod");
        }) {
            private boolean wasHovered;

            @Override
            public void render(MatrixStack stack, int p_render_1_, int p_render_2_, float p_render_3_) {
                if (this.visible) {
                    this.isHovered = p_render_1_ >= this.x && p_render_2_ >= this.y && p_render_1_ < this.x + this.width && p_render_2_ < this.y + this.height;
                    if (this.wasHovered != this.isHovered()) {
                        if (this.isHovered()) {
                            if (this.isFocused()) {
                                this.nextNarration = Util.milliTime() + 200L;
                            } else {
                                this.nextNarration = Util.milliTime() + 750L;
                            }
                        } else {
                            this.nextNarration = Long.MAX_VALUE;
                        }
                    }

                    if (this.visible) {
                        this.renderButton(stack, p_render_1_, p_render_2_, p_render_3_);
                    }

                    this.narrate();
                    this.wasHovered = this.isHovered();
                }
            }
        });
        sortOrder = SortOrder.NONE;
        Button buttonSort = this.addButton(new Button(this.width / 2 - 155 + 160 + 76, this.height - 29 - 24 - 24, 150 / 2, 20, new TranslationTextComponent("options.sort").appendString(": " + sortOrder.getName()), (p_213126_1_) -> {
            sortOrder = sortOrder.cycle();
            p_213126_1_.setMessage(new TranslationTextComponent("options.sort").appendString(": " + sortOrder.getName()));
            filterKeys();
        }));
        lastSearch = "";
        displayMode = DisplayMode.ALL;
        searchType = SearchType.NAME;

    }

    @Override
    public boolean charTyped(char var1, int var2) {
        return search.charTyped(var1, var2);
    }

    public void tick() {
        this.search.tick();
        if (!lastSearch.equals(search.getText())) {
            filterKeys();
        }
    }

    public void filterKeys() {

        lastSearch = search.getText();
        keyBindingList.getEventListeners().clear();
        if (lastSearch.isEmpty() && displayMode == DisplayMode.ALL && sortOrder == SortOrder.NONE) {
            keyBindingList.getEventListeners().addAll(((GuiNewKeyBindingList) keyBindingList).getAllEntries());
            return;
        }
        this.keyBindingList.setScrollAmount(0);
        Predicate<GuiNewKeyBindingList.KeyEntry> filters = displayMode.getPredicate();


        switch (searchType) {
            case NAME:
                filters = filters.and(keyEntry -> keyEntry.getKeyDesc().toLowerCase().contains(lastSearch.toLowerCase()));
                break;
            case CATEGORY:
                filters = filters.and(keyEntry -> new TranslationTextComponent(keyEntry.getKeybinding().getKeyCategory()).getString().toLowerCase().contains(lastSearch.toLowerCase()));
                break;
            case KEY:
                filters = filters.and(keyEntry -> keyEntry.getKeybinding().func_238171_j_().getString().toLowerCase().contains(lastSearch.toLowerCase()));
                break;
        }

        for (GuiNewKeyBindingList.Entry entry : ((GuiNewKeyBindingList) keyBindingList).getAllEntries()) {
            if (searchType == SearchType.CATEGORY && sortOrder == SortOrder.NONE && displayMode == DisplayMode.ALL) {
                if (entry instanceof GuiNewKeyBindingList.KeyEntry) {
                    GuiNewKeyBindingList.KeyEntry keyEntry = (GuiNewKeyBindingList.KeyEntry) entry;
                    if (filters.test(keyEntry)) {
                        keyBindingList.getEventListeners().add(entry);
                    }
                } else {
                    keyBindingList.getEventListeners().add(entry);
                }
            } else {
                if (entry instanceof GuiNewKeyBindingList.KeyEntry) {
                    GuiNewKeyBindingList.KeyEntry keyEntry = (GuiNewKeyBindingList.KeyEntry) entry;
                    if (filters.test(keyEntry)) {
                        keyBindingList.getEventListeners().add(entry);
                    }
                }
            }

        }
        if (searchType == SearchType.CATEGORY && sortOrder == SortOrder.NONE && displayMode == DisplayMode.ALL) {
            Set<GuiNewKeyBindingList.CategoryEntry> categories = new LinkedHashSet<>();

            for (KeyBindingList.Entry entry : keyBindingList.getEventListeners()) {
                if (entry instanceof GuiNewKeyBindingList.CategoryEntry) {
                    GuiNewKeyBindingList.CategoryEntry centry = (GuiNewKeyBindingList.CategoryEntry) entry;
                    categories.add(centry);
                    for (KeyBindingList.Entry child : keyBindingList.getEventListeners()) {
                        if (child instanceof GuiNewKeyBindingList.KeyEntry) {
                            GuiNewKeyBindingList.KeyEntry childEntry = (GuiNewKeyBindingList.KeyEntry) child;
                            if (childEntry.getKeybinding().getKeyCategory().equals(centry.getName())) {
                                categories.remove(centry);
                            }
                        }
                    }
                }
            }
            keyBindingList.getEventListeners().removeAll(categories);
        }
        sortOrder.sort(keyBindingList.getEventListeners());


    }

    /**
     * Draws the screen and all the components in it.
     */
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.keyBindingList.render(stack, mouseX, mouseY, partialTicks);
        this.drawCenteredString(stack, this.font, this.title.getString(), this.width / 2, 8, 16777215);
        boolean flag = false;

        for (KeyBinding keybinding : this.options.keyBindings) {
            if (!keybinding.isDefault()) {
                flag = true;
                break;
            }
        }
        search.render(stack, mouseX, mouseY, partialTicks);
        this.buttonReset.active = flag;
        if (!flag) {
            confirmingReset = false;
            buttonReset.setMessage(new TranslationTextComponent("controls.resetAll"));
        }
        for (int i = 0; i < this.buttons.size(); ++i) {
            this.buttons.get(i).render(stack, mouseX, mouseY, partialTicks);
        }

        ITextComponent text = new TranslationTextComponent("options.search");
        font.func_238407_a_(stack, text.func_241878_f(), this.width / 2f - (155 / 2f) - (font.getStringWidth(text.getString())) - 5, this.height - 29 - 42, 16777215);

        if (patreonButton.isHovered()) {
            String str = "Join " + name + " and other patrons!";
            renderTooltip(stack, new StringTextComponent(str), mouseX, mouseY);
        }
    }

    public boolean mouseClicked(double mx, double my, int mb) {
        boolean valid;
        if (this.buttonId != null) {
            this.options.setKeyBindingCode(this.buttonId, InputMappings.Type.MOUSE.getOrMakeInput(mb));
            this.buttonId = null;
            KeyBinding.resetKeyBindingArrayAndHash();
            valid = true;
            search.setFocused2(false);
        } else if (mb == 0 && this.keyBindingList.mouseClicked(mx, my, mb)) {
            this.setDragging(true);
            this.setListener(this.keyBindingList);
            valid = true;
            search.setFocused2(false);
        } else {
            valid = search.mouseClicked(mx, my, mb);
            if (!valid && search.isFocused() && mb == 1) {
                search.setText("");
                valid = true;
            }
        }

        if (!valid) {

            for (IGuiEventListener iguieventlistener : this.getEventListeners()) {
                if (iguieventlistener.mouseClicked(mx, my, mb)) {
                    this.setListener(iguieventlistener);
                    if (mb == 0) {
                        this.setDragging(true);
                    }

                    return true;
                }
            }

            valid = true;
        }


        return valid;
    }

    public boolean mouseReleased(double mx, double my, int mb) {
        if (mb == 0 && this.keyBindingList.mouseReleased(mx, my, mb)) {
            this.setDragging(false);
            return true;
        } else if (search.isFocused()) {
            return search.mouseReleased(mx, my, mb);
        } else {
            this.setDragging(false);
            return false;
        }
    }

    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (!search.isFocused() && this.buttonId == null) {
            if (hasControlDown()) {
                if (InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), GLFW.GLFW_KEY_F)) {
                    search.setFocused2(true);
                    return true;
                }
            }
        }
        if (search.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
            return true;
        }
        if (search.isFocused()) {
            if (p_keyPressed_1_ == 256) {
                search.setFocused2(false);
                return true;
            }
        }
        if (this.buttonId != null) {
            if (p_keyPressed_1_ == 256) {
                this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), InputMappings.INPUT_INVALID);
                this.options.setKeyBindingCode(this.buttonId, InputMappings.INPUT_INVALID);
            } else {
                this.buttonId.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.getActiveModifier(), InputMappings.getInputByCode(p_keyPressed_1_, p_keyPressed_2_));
                this.options.setKeyBindingCode(this.buttonId, InputMappings.getInputByCode(p_keyPressed_1_, p_keyPressed_2_));
            }

            if (!net.minecraftforge.client.settings.KeyModifier.isKeyCodeModifier(this.buttonId.getKey()))
                this.buttonId = null;
            this.time = Util.milliTime();
            KeyBinding.resetKeyBindingArrayAndHash();
            return true;
        } else {
            return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }
    }


}