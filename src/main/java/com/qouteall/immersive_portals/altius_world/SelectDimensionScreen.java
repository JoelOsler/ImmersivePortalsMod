package com.qouteall.immersive_portals.altius_world;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GeneratorOptions;

import java.util.function.Consumer;

public class SelectDimensionScreen extends Screen {
    public final AltiusScreen parent;
    private DimListWidget dimListWidget;
    private ButtonWidget confirmButton;
    private Consumer<RegistryKey<World>> outerCallback;
    
    protected SelectDimensionScreen(AltiusScreen parent, Consumer<RegistryKey<World>> callback) {
        super(new TranslatableText("imm_ptl.select_dimension"));
        this.parent = parent;
        this.outerCallback = callback;
    }
    
    @Override
    protected void init() {
        dimListWidget = new DimListWidget(
            width,
            height,
            48,
            height - 64,
            15,
            this
        );
        children.add(dimListWidget);
        
        Consumer<DimTermWidget> callback = w -> dimListWidget.setSelected(w);
        
        GeneratorOptions generatorOptions =
            parent.parent.moreOptionsDialog.getGeneratorOptions(false);
        
        SimpleRegistry<DimensionOptions> dimensionMap = generatorOptions.getDimensionMap();
        
        dimensionMap.getIds().forEach(dimId -> {
            RegistryKey<World> worldKey = RegistryKey.of(Registry.DIMENSION, dimId);
            dimListWidget.terms.add(new DimTermWidget(worldKey, dimListWidget, callback));
        });
        
        dimListWidget.update();
        
        confirmButton = (ButtonWidget) this.addButton(new ButtonWidget(
            this.width / 2 - 75, this.height - 28, 150, 20,
            new TranslatableText("imm_ptl.confirm_select_dimension"),
            (buttonWidget) -> {
                DimTermWidget selected = dimListWidget.getSelected();
                if (selected == null) {
                    return;
                }
                outerCallback.accept(selected.dimension);
                MinecraftClient.getInstance().openScreen(parent);
            }
        ));
        
    }
    
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrixStack);
        
        dimListWidget.render(matrixStack, mouseX, mouseY, delta);
        
        super.render(matrixStack, mouseX, mouseY, delta);
        
        this.drawCenteredString(
            matrixStack, this.textRenderer, this.title.asString(), this.width / 2, 20, -1
        );
    }
}
