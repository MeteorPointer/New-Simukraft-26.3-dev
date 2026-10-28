package common.cn.nsimu.simukraft.debugcity;

import com.mojang.serialization.Codec;
import common.cn.nsimu.simukraft.SimuKraft;
import common.cn.nsimu.simukraft.util.NbtUuid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.List;
import java.util.UUID;

/**
 * 调试城市的生成进度，避免重进世界后重复铺城。
 */
public final class DebugCityProgress extends SavedData {
    private static final Identifier DATA_ID = Identifier.fromNamespaceAndPath(SimuKraft.MOD_ID, "debug_city");
    private static final SavedDataType<DebugCityProgress> TYPE = createType();
    private static final int PHASE_WAITING = 0;
    private static final int PHASE_CLAIMING = 1;
    private static final int PHASE_POPULATING = 2;

    private static SavedDataType<DebugCityProgress> createType() {
        Codec<DebugCityProgress> codec = CompoundTag.CODEC.xmap(DebugCityProgress::load, DebugCityProgress::serializeToTag);
        return new SavedDataType<>(DATA_ID, DebugCityProgress::new, codec);
    }

    private CompoundTag serializeToTag() {
        return save(new CompoundTag());
    }

    private boolean configured;
    private boolean done;
    private boolean announced;
    private int phase = PHASE_WAITING;
    private int citizenCount = 1;
    private int side = 1;
    private int claimIndex;
    private int citizenIndex;
    private UUID cityId;
    private transient List<DebugCityLayout.Cell> occupiedCells;

    public DebugCityProgress() {
    }

    /**
     * get：读取当前世界的调试城市进度。
     */
    public static DebugCityProgress get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    private static DebugCityProgress load(CompoundTag tag) {
        DebugCityProgress progress = new DebugCityProgress();
        progress.configured = tag.getBooleanOr("Configured", false);
        progress.done = tag.getBooleanOr("Done", false);
        progress.announced = tag.getBooleanOr("Announced", false);
        progress.phase = tag.getIntOr("Phase", PHASE_WAITING);
        progress.citizenCount = tag.getIntOr("CitizenCount", 1);
        progress.side = tag.getIntOr("Side", 1);
        progress.claimIndex = tag.getIntOr("ClaimIndex", 0);
        progress.citizenIndex = tag.getIntOr("CitizenIndex", 0);
        progress.cityId = NbtUuid.readOrNull(tag, "CityId");
        return progress;
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("Configured", configured);
        tag.putBoolean("Done", done);
        tag.putBoolean("Announced", announced);
        tag.putInt("Phase", phase);
        tag.putInt("CitizenCount", citizenCount);
        tag.putInt("Side", side);
        tag.putInt("ClaimIndex", claimIndex);
        tag.putInt("CitizenIndex", citizenIndex);
        if (cityId != null) {
            NbtUuid.put(tag, "CityId", cityId);
        }
        return tag;
    }

    public boolean done() {
        return done;
    }

    public boolean announced() {
        return announced;
    }

    public boolean waitingForPlayer() {
        return phase == PHASE_WAITING;
    }

    public boolean claiming() {
        return phase == PHASE_CLAIMING;
    }

    public boolean populating() {
        return phase == PHASE_POPULATING;
    }

    public int citizenCount() {
        return citizenCount;
    }

    public int side() {
        return side;
    }

    public int claimIndex() {
        return claimIndex;
    }

    public int citizenIndex() {
        return citizenIndex;
    }

    public UUID cityId() {
        return cityId;
    }

    /**
     * configure：第一次进入时记下居民数和网格边长。
     */
    public void configure(int citizens, int cityScale) {
        this.citizenCount = DebugCityLayout.clampCitizens(citizens);
        this.side = DebugCityLayout.sideLength(this.citizenCount, cityScale);
        this.configured = true;
        setDirty();
    }

    public boolean configured() {
        return configured;
    }

    public void markAnnounced() {
        this.announced = true;
        setDirty();
    }

    public void beginClaiming(UUID city) {
        this.cityId = city;
        this.phase = PHASE_CLAIMING;
        this.claimIndex = 0;
        setDirty();
    }

    public void advanceClaim() {
        this.claimIndex++;
        if (this.claimIndex >= this.side * this.side) {
            this.phase = PHASE_POPULATING;
        }
        setDirty();
    }

    public void advanceCitizen() {
        this.citizenIndex++;
        if (this.citizenIndex >= this.citizenCount) {
            this.done = true;
        }
        setDirty();
    }

    public void finish() {
        this.done = true;
        setDirty();
    }

    /**
     * occupiedCells：按本次快照的规模返回居民地块，顺序在一次生成中保持不变。
     */
    public List<DebugCityLayout.Cell> occupiedCells() {
        if (occupiedCells == null) {
            occupiedCells = DebugCityLayout.occupiedCells(citizenCount, side);
        }
        return occupiedCells;
    }
}
