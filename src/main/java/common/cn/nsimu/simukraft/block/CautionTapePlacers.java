package common.cn.nsimu.simukraft.block;

import com.mojang.serialization.Codec;
import common.cn.nsimu.simukraft.SimuKraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * CautionTapePlacers: 每格警戒线记住放置者，用来反查他所属的城市。
 */
public final class CautionTapePlacers extends SavedData {
    private static final Identifier DATA_ID = Identifier.fromNamespaceAndPath(SimuKraft.MOD_ID, "caution_tape_placers");
    private static final SavedDataType<CautionTapePlacers> TYPE = createType();

    private static SavedDataType<CautionTapePlacers> createType() {
        Codec<CautionTapePlacers> codec = CompoundTag.CODEC.xmap(CautionTapePlacers::load, CautionTapePlacers::serializeToTag);
        return new SavedDataType<>(DATA_ID, CautionTapePlacers::new, codec);
    }

    private final ConcurrentMap<BlockPos, UUID> placers = new ConcurrentHashMap<>();

    public static CautionTapePlacers get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public void set(BlockPos pos, @Nullable UUID placerId) {
        if (pos == null) {
            return;
        }
        if (placerId == null) {
            if (placers.remove(pos) != null) {
                setDirty();
                CautionTapeZones.rebuild(pos);
            }
            return;
        }
        placers.put(pos.immutable(), placerId);
        setDirty();
        CautionTapeZones.rebuild(pos);
    }

    @Nullable
    public UUID get(BlockPos pos) {
        return pos == null ? null : placers.get(pos);
    }

    public Map<BlockPos, UUID> entries() {
        return Map.copyOf(placers);
    }

    public void remove(BlockPos pos) {
        set(pos, null);
    }

    private CompoundTag serializeToTag() {
        return save(new CompoundTag());
    }

    private static CautionTapePlacers load(CompoundTag tag) {
        CautionTapePlacers data = new CautionTapePlacers();
        ListTag list = tag.getList("Placers").orElse(new ListTag());
        for (int index = 0; index < list.size(); index++) {
            CompoundTag entry = list.getCompound(index).orElse(new CompoundTag());
            String placer = entry.getString("Placer").orElse("");
            if (placer.isBlank()) {
                continue;
            }
            data.placers.put(BlockPos.of(entry.getLongOr("Pos", 0L)), UUID.fromString(placer));
        }
        return data;
    }

    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        placers.forEach((pos, placer) -> {
            CompoundTag entry = new CompoundTag();
            entry.putLong("Pos", pos.asLong());
            entry.putString("Placer", placer.toString());
            list.add(entry);
        });
        tag.put("Placers", list);
        return tag;
    }
}
