/*
 * Copyright (c) 2018-2024 C4
 *
 * This file is part of Curios, a mod made for Minecraft.
 *
 * Curios is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Curios is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Curios.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package top.theillusivec4.curios.common.network.server.sync;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.CuriosConstants;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public class SPacketSyncCurios implements CustomPacketPayload {

  public static final Type<SPacketSyncCurios> TYPE =
      new Type<>(ResourceLocation.fromNamespaceAndPath(CuriosConstants.MOD_ID, "sync_curios"));
  public static final StreamCodec<RegistryFriendlyByteBuf, SPacketSyncCurios> STREAM_CODEC =
      new StreamCodec<>() {
        @Nonnull
        @Override
        public SPacketSyncCurios decode(@Nonnull RegistryFriendlyByteBuf buf) {
          return new SPacketSyncCurios(buf);
        }

        @Override
        public void encode(@Nonnull RegistryFriendlyByteBuf buf, SPacketSyncCurios packet) {
          buf.writeInt(packet.entityId);
          buf.writeInt(packet.entrySize);

          for (Map.Entry<String, CompoundTag> entry : packet.map.entrySet()) {
            buf.writeUtf(entry.getKey());
            buf.writeNbt(entry.getValue());
          }
        }
      };

  public final int entityId;
  public final int entrySize;
  public final Map<String, CompoundTag> map;

  public SPacketSyncCurios(int entityId, Map<String, ICurioStacksHandler> map) {
    Map<String, CompoundTag> result = new LinkedHashMap<>();

    for (Map.Entry<String, ICurioStacksHandler> entry : map.entrySet()) {
      result.put(entry.getKey(), entry.getValue().getSyncTag());
    }
    this.entityId = entityId;
    this.entrySize = map.size();
    this.map = result;
  }

  public SPacketSyncCurios(final FriendlyByteBuf buf) {
    int entityId = buf.readInt();
    int entrySize = buf.readInt();
    Map<String, CompoundTag> map = new LinkedHashMap<>();

    for (int i = 0; i < entrySize; i++) {
      String key = buf.readUtf();
      map.put(key, buf.readNbt());
    }
    this.entityId = entityId;
    this.entrySize = map.size();
    this.map = map;
  }

  @Nonnull
  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
