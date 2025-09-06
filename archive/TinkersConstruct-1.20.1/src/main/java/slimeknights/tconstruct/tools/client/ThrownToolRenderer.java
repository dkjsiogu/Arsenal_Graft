package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import slimeknights.tconstruct.library.TinkerItemDisplays;
import slimeknights.tconstruct.tools.entity.ThrownTool;

/** Renderer for {@link ThrownTool} */
public class ThrownToolRenderer extends EntityRenderer<ThrownTool> {
  protected final ItemRenderer itemRenderer;
  public ThrownToolRenderer(Context context) {
    super(context);
    this.itemRenderer = context.getItemRenderer();
  }

  @Override
  public void render(ThrownTool entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
    poseStack.pushPose();
    poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90));
    poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 225));
    poseStack.translate(0.2, -0.2, 0);
    this.itemRenderer.renderStatic(entity.getToolItem(), TinkerItemDisplays.THROWN, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
    poseStack.popPose();
    super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight);
  }

  @Override
  public ResourceLocation getTextureLocation(ThrownTool entity) {
    return InventoryMenu.BLOCK_ATLAS;
  }
}
