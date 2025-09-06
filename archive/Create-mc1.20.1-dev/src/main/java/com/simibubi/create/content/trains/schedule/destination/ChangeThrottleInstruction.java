package com.simibubi.create.content.trains.schedule.destination;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime.State;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.data.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChangeThrottleInstruction extends ScheduleInstruction {

	public ChangeThrottleInstruction() {
		super();
		data.putInt("Value", 100);
	}

	@Override
	public Pair<ItemStack, Component> getSummary() {
		return Pair.of(icon(), formatted());
	}

	private MutableComponent formatted() {
		return Component.literal(intData("Value") + "%");
	}

	@Override
	public ResourceLocation getId() {
		return Create.asResource("throttle");
	}

	@Override
	public ItemStack getSecondLineIcon() {
		return icon();
	}

	@Override
	public boolean supportsConditions() {
		return false;
	}

	@Override
	public List<Component> getTitleAs(String type) {
		return ImmutableList.of(CreateLang
			.translateDirect("schedule." + type + "." + getId().getPath() + ".summary",
				formatted().withStyle(ChatFormatting.WHITE))
			.withStyle(ChatFormatting.GOLD));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
		builder.addScrollInput(0, 50, (si, l) -> {
			si.withRange(5, 101)
				.withStepFunction(c -> c.shift ? 25 : 5)
				.titled(CreateLang.translateDirect("schedule.instruction.throttle_edit_box"));
			l.withSuffix("%");
		}, "Value");
	}

	public float getThrottle() {
		return intData("Value") / 100f;
	}

	private ItemStack icon() {
		return AllBlocks.TRAIN_CONTROLS.asStack();
	}

	@Override
	public List<Component> getSecondLineTooltip(int slot) {
		return ImmutableList.of(CreateLang.translateDirect("schedule.instruction.throttle_edit_box"),
			CreateLang.translateDirect("schedule.instruction.throttle_edit_box_1")
				.withStyle(ChatFormatting.GRAY));
	}

	@Override
	@Nullable
	public DiscoveredPath start(ScheduleRuntime runtime, Level level) {
		runtime.train.throttle = getThrottle();
		runtime.state = State.PRE_TRANSIT;
		runtime.currentEntry++;
		return null;
	}

}
