package com.mgblackhole;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = MGBlackHole.MOD_ID)
public final class BlackHoleField {
    private static final Map<ServerLevel, List<Field>> FIELDS = new WeakHashMap<>();
    private static final double RADIUS = 9.0;
    private static final int DURATION_TICKS = 200;

    private BlackHoleField() {}

    public static void create(Level level, Vec3 position, Entity owner) {
        if (level instanceof ServerLevel serverLevel) {
            FIELDS.computeIfAbsent(serverLevel, ignored -> new ArrayList<>())
                    .add(new Field(position, DURATION_TICKS));
        }
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) return;
        List<Field> fields = FIELDS.get(level);
        if (fields == null || fields.isEmpty()) return;

        Iterator<Field> iterator = fields.iterator();
        while (iterator.hasNext()) {
            Field field = iterator.next();
            field.age++;
            if (field.age >= field.duration) {
                iterator.remove();
                continue;
            }

            Vec3 center = field.position;
            AABB area = new AABB(center, center).inflate(RADIUS);
            for (Entity entity : level.getEntities((Entity) null, area,
                    e -> e.isAlive() && e.distanceToSqr(center) < RADIUS * RADIUS)) {
                Vec3 difference = center.subtract(entity.position());
                double distance = Math.max(0.8, difference.length());
                double strength = entity instanceof ItemEntity ? 0.16 : 0.075;
                Vec3 pull = difference.normalize().scale(strength * Math.min(1.8, 5.0 / distance));
                entity.setDeltaMovement(entity.getDeltaMovement().scale(0.92).add(pull));
                entity.hurtMarked = true;
            }

            double rotation = field.age * 0.18;
            for (int i = 0; i < 18; i++) {
                double angle = rotation + Math.PI * 2.0 * i / 18.0;
                double radius = 1.0 + 0.12 * Math.sin(field.age * 0.15 + i);
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;
                level.sendParticles(ParticleTypes.PORTAL, center.x + x, center.y + 0.15 * Math.sin(angle * 2), center.z + z, 1, 0, 0, 0, 0.01);
                if (i % 3 == 0) {
                    level.sendParticles(ParticleTypes.END_ROD, center.x + x * 1.15, center.y, center.z + z * 1.15, 1, 0, 0, 0, 0);
                }
            }
            level.sendParticles(ParticleTypes.REVERSE_PORTAL, center.x, center.y, center.z, 5, 0.18, 0.18, 0.18, 0.02);
        }
        if (fields.isEmpty()) FIELDS.remove(level);
    }

    private static final class Field {
        private final Vec3 position;
        private final int duration;
        private int age;
        private Field(Vec3 position, int duration) {
            this.position = position;
            this.duration = duration;
        }
    }
}
