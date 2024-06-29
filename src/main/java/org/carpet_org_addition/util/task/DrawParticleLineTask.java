/*
 * This file is part of the Carpet Org Addition project, licensed under the
 * MIT License
 *
 * Copyright (c) 2024 cdqtzrc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.carpet_org_addition.util.task;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class DrawParticleLineTask extends ServerTask {
    private final ServerWorld world;
    private final ParticleEffect particleEffect;
    private final double distance;
    // 粒子线的起点
    private final Vec3d from;
    // 粒子线延伸的方向
    private final Vec3d vector;
    private Vec3d origin = new Vec3d(0.0, 0.0, 0.0);

    public DrawParticleLineTask(ServerWorld world, ParticleEffect particleEffect, Vec3d from, Vec3d to) {
        this.world = world;
        this.particleEffect = particleEffect;
        this.from = from;
        this.distance = from.squaredDistanceTo(to);
        this.vector = to.subtract(this.from).normalize();
    }

    @Override
    public void tick() {
        // 每一个游戏刻内需要绘制的距离
        double tickDistance = Math.sqrt(distance) / 20;
        double sum = 0;
        // 每次绘制0.5格，直到总距离达到每一个游戏刻内需要绘制的距离
        while (sum < tickDistance) {
            this.spawnParticles();
            this.origin = this.origin.add(this.vector.multiply(0.5));
            sum += 0.5;
        }
    }

    // 生成粒子效果
    private void spawnParticles() {
        this.world.spawnParticles(this.particleEffect,
                this.from.x + this.origin.x,
                this.from.y + this.origin.y,
                this.from.z + this.origin.z,
                5, 0, 0, 0, 1);
    }

    @Override
    public boolean isEndOfExecution() {
        return this.distance <= this.origin.lengthSquared();
    }
}
