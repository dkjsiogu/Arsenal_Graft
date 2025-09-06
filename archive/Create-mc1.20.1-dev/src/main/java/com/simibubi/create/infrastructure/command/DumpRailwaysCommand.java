package com.simibubi.create.infrastructure.command;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.station.GlobalStation;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DumpRailwaysCommand {

	private static final int white = ChatFormatting.WHITE.getColor();
	private static final int blue = 0xaac8e0;
	//private static final int blue = 0xD3DEDC;
	private static final int darkBlue = 0x88a5b7;
	//private static final int darkBlue = 0x92A9BD;
	private static final int darkerBlue = 0x6b8694;
	private static final int darkestBlue = 0x536b75;
	private static final int bright = 0xFFEFEF;
	private static final int orange = 0xFFAD60;

	static ArgumentBuilder<CommandSourceStack, ?> register() {
		return Commands.literal("trains")
			.requires(cs -> cs.hasPermission(2))
			.executes(ctx -> {
				CommandSourceStack source = ctx.getSource();
				fillReport(source.getLevel(), source.getPosition(),
					(s, f) -> source.sendSuccess(() -> {
						return Component.literal(s).withStyle(st -> st.withColor(f));
					}, false),
					(c) -> source.sendSuccess(() -> c, false));
				return 1;
			});
	}

	// https://www.compart.com/en/unicode/search?q=box+drawings+light#characters ┬ ├ └ ─
	static void fillReport(ServerLevel level, Vec3 location, BiConsumer<String, Integer> chat,
						   Consumer<Component> chatRaw) {
		GlobalRailwayManager railways = Create.RAILWAYS;


		chat.accept("", white);
		chat.accept("-+------<< Train Summary: >>------+-", white);
		int graphCount = railways.trackNetworks.size();
		chat.accept("Track Networks: " + graphCount, blue);
		chat.accept("Signal Groups: " + railways.signalEdgeGroups.size(), blue);
		int trainCount = railways.trains.size();
		chat.accept("Trains: " + trainCount, blue);
		chat.accept("", white);

		List<TrackGraph> nearest = railways.trackNetworks.values()
			.stream()
			.sorted((tg1, tg2) -> Float.compare(tg1.distanceToLocationSqr(level, location),
				tg2.distanceToLocationSqr(level, location)))
			.limit(5)
			.toList();

		if (graphCount > 0) {
			chat.accept("Nearest Graphs: ", orange);
			chat.accept("", white);
			for (TrackGraph graph : nearest) {
				chat.accept(graph.id.toString()
					.substring(0, 5) + " with "
					+ graph.getNodes()
					.size()
					+ " Nodes", white);
				Collection<SignalBoundary> signals = graph.getPoints(EdgePointType.SIGNAL);
				if (!signals.isEmpty())
					chat.accept(" -> " + signals.size() + " Signals", blue);
				Collection<GlobalStation> stations = graph.getPoints(EdgePointType.STATION);
				if (!stations.isEmpty())
					chat.accept(" -> " + stations.size() + " Stations", blue);
			}
			chat.accept("", white);
			if (graphCount > 5) {
				chat.accept("[...]", white);
				chat.accept("", white);
			}
		}

		List<Train> nearestTrains = railways.trains.values()
			.stream()
			.sorted((t1, t2) -> Float.compare(t1.distanceToLocationSqr(level, location),
				t2.distanceToLocationSqr(level, location)))
			.limit(5)
			.toList();

		if (trainCount > 0 && !nearestTrains.isEmpty()) {
			chat.accept("Nearest Trains: ", orange);
			chat.accept("", white);
			for (Train train : nearestTrains) {
				chat.accept(String.format("┬%1$s: %2$s, %3$d Wagons",
					train.id.toString().substring(0, 5),
					train.name.getString(),
					train.carriages.size()
				), bright);
				if (train.derailed)
					chat.accept("├─Derailed", orange);
				else if (train.graph != null)
					chat.accept("├─On Track: " + train.graph.id.toString()
						.substring(0, 5), blue);
				LivingEntity owner = train.getOwner(level);
				if (owner != null)
					chat.accept("├─Owned by " + owner.getName()
						.getString(), blue);
				GlobalStation currentStation = train.getCurrentStation();
				if (currentStation != null) {
					chat.accept("├─Waiting at: " + currentStation.name, blue);
				} else if (train.navigation.destination != null)
					chat.accept("├─Travelling to " + train.navigation.destination.name + " ("
						+ Mth.floor(train.navigation.distanceToDestination) + "m away)", darkBlue);
				ScheduleRuntime runtime = train.runtime;
				if (runtime.getSchedule() != null) {
					chat.accept("├─Schedule, Entry " + runtime.currentEntry + ", "
							+ (runtime.paused ? "Paused"
							: runtime.state.name()
							.replaceAll("_", " ")),
						runtime.paused ? darkBlue : blue);
				} else
					chat.accept("├─Idle, No Schedule", darkBlue);

				List<ResourceKey<Level>> presentDimensions = train.getPresentDimensions();
				if (presentDimensions.size() > 1)
					chat.accept("├─Travelling between Dimensions:", darkerBlue);
				presentDimensions.forEach(key ->
					chat.accept("├─In %1$s near [%2$s]".formatted(key.location(), train.getPositionInDimension(key).get().toShortString()), darkerBlue)
				);
				chatRaw.accept(createTeleportButton(train));

				chatRaw.accept(createDeleteButton(train));
				chat.accept("", white);
			}
			if (trainCount > 5) {
				chat.accept("[...]", white);
				chat.accept("", white);
			}
		}

		chat.accept("-+--------------------------------+-", white);
	}

	private static Component createDeleteButton(Train train) {
		return Component.literal("└─").withStyle(style -> style.withColor(blue)).append(
			ComponentUtils.wrapInSquareBrackets(
				Component.literal("Remove").withStyle(style -> style.withColor(orange))
			).withStyle(style -> {
					return style
						.withColor(blue)
						.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/c train remove " + train.id.toString()))
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to remove ").append(train.name)));
				}
			)
		);
	}

	private static Component createTeleportButton(Train train) {
		return Component.literal("├─").withStyle(style -> style.withColor(darkBlue)).append(
			ComponentUtils.wrapInSquareBrackets(
				Component.literal("Teleport").withStyle(style -> style.withColor(orange))
			).withStyle(style -> {
					return style
						.withColor(darkBlue)
						.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/c train tp " + train.id.toString()))
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to teleport to ").append(train.name)));
				}
			)
		);
	}

}
