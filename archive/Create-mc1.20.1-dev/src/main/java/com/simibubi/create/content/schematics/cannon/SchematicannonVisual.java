package com.simibubi.create.content.schematics.cannon;

import java.util.function.Consumer;

import com.simibubi.create.AllPartialModels;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.core.Direction;

public class SchematicannonVisual extends AbstractBlockEntityVisual<SchematicannonBlockEntity> implements SimpleDynamicVisual {

    private final TransformedInstance connector;
    private final TransformedInstance pipe;

	private double lastYaw = Double.NaN;
	private double lastPitch = Double.NaN;
	private double lastRecoil = Double.NaN;

    public SchematicannonVisual(VisualizationContext context, SchematicannonBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        connector = instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.SCHEMATICANNON_CONNECTOR)).createInstance();
        pipe = instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.SCHEMATICANNON_PIPE)).createInstance();

		animate(partialTick);
	}

    @Override
    public void beginFrame(DynamicVisual.Context ctx) {
		animate(ctx.partialTick());
	}

	private void animate(float partialTicks) {
		double[] cannonAngles = SchematicannonRenderer.getCannonAngles(blockEntity, pos, partialTicks);

		double yaw = cannonAngles[0];
		double pitch = cannonAngles[1];

		double recoil = SchematicannonRenderer.getRecoil(blockEntity, partialTicks);

		if (yaw != lastYaw) {
			connector.setIdentityTransform()
				.translate(getVisualPosition())
				.center()
				.rotate((float) ((yaw + 90) / 180 * Math.PI), Direction.UP)
				.uncenter()
				.setChanged();
		}

		if (pitch != lastPitch || recoil != lastRecoil) {
			pipe.setIdentityTransform()
				.translate(getVisualPosition())
				.translate(.5f, 15 / 16f, .5f)
				.rotate((float) ((yaw + 90) / 180 * Math.PI), Direction.UP)
				.rotate((float) (pitch / 180 * Math.PI), Direction.SOUTH)
				.translateBack(.5f, 15 / 16f, .5f)
				.translate(0, -recoil / 100, 0)
				.setChanged();
		}

		lastYaw = yaw;
		lastPitch = pitch;
		lastRecoil = recoil;
	}

	@Override
    protected void _delete() {
        connector.delete();
        pipe.delete();
    }

    @Override
    public void updateLight(float partialTick) {
        relight(connector, pipe);
    }

	@Override
	public void collectCrumblingInstances(Consumer<Instance> consumer) {
		consumer.accept(connector);
		consumer.accept(pipe);
	}
}
