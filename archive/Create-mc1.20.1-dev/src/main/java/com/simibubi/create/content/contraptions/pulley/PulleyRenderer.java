package com.simibubi.create.content.contraptions.pulley;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;

public class PulleyRenderer extends AbstractPulleyRenderer<PulleyBlockEntity> {

	public PulleyRenderer(BlockEntityRendererProvider.Context context) {
		super(context, AllPartialModels.ROPE_HALF, AllPartialModels.ROPE_HALF_MAGNET);
	}

	@Override
	protected Axis getShaftAxis(PulleyBlockEntity be) {
		return be.getBlockState()
			.getValue(PulleyBlock.HORIZONTAL_AXIS);
	}

	@Override
	protected PartialModel getCoil() {
		return AllPartialModels.ROPE_COIL;
	}

	@Override
	protected SuperByteBuffer renderRope(PulleyBlockEntity be) {
		return CachedBuffers.block(AllBlocks.ROPE.getDefaultState());
	}

	@Override
	protected SuperByteBuffer renderMagnet(PulleyBlockEntity be) {
		return CachedBuffers.block(AllBlocks.PULLEY_MAGNET.getDefaultState());
	}

	@Override
	protected float getOffset(PulleyBlockEntity be, float partialTicks) {
		return getBlockEntityOffset(partialTicks, be);
	}

	@Override
	protected boolean isRunning(PulleyBlockEntity be) {
		return isPulleyRunning(be);
	}

	public static boolean isPulleyRunning(PulleyBlockEntity be) {
		return be.running || be.mirrorParent != null || be.isVirtual();
	}

	@Override
	protected SpriteShiftEntry getCoilShift() {
		return AllSpriteShifts.ROPE_PULLEY_COIL;
	}

	public static float getBlockEntityOffset(float partialTicks, PulleyBlockEntity blockEntity) {
		float offset = blockEntity.getInterpolatedOffset(partialTicks);

		AbstractContraptionEntity attachedContraption = blockEntity.getAttachedContraption();
		if (attachedContraption != null) {
			PulleyContraption c = (PulleyContraption) attachedContraption.getContraption();
			double entityPos = Mth.lerp(partialTicks, attachedContraption.yOld, attachedContraption.getY());
			offset = (float) -(entityPos - c.anchor.getY() - c.getInitialOffset());
		}

		return offset;
	}

}
