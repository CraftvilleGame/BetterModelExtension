package entries.entity

import com.typewritermc.engine.paper.entry.entity.EntityState
import com.typewritermc.engine.paper.entry.entity.FakeEntity
import com.typewritermc.engine.paper.entry.entity.PositionProperty
import com.typewritermc.engine.paper.entry.entries.EntityProperty
import com.typewritermc.engine.paper.entry.entries.Var
import com.typewritermc.engine.paper.plugin
import com.typewritermc.engine.paper.utils.toBukkitLocation
import com.typewritermc.entity.entries.data.minecraft.GlowingEffectProperty
import com.typewritermc.entity.entries.data.minecraft.SpeedProperty
import com.typewritermc.entity.entries.data.minecraft.living.ScaleProperty
import com.typewritermc.entity.entries.data.minecraft.living.armorstand.InvisibleProperty
import com.typewritermc.entity.entries.entity.minecraft.InteractionEntity
import entries.entity.definition.DefaultAnimationSettings
import io.github.retrooper.packetevents.util.SpigotReflectionUtil
import kr.toxicity.model.api.BetterModel
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter
import kr.toxicity.model.api.tracker.DummyTracker
import kr.toxicity.model.api.tracker.ModelScaler
import kr.toxicity.model.api.tracker.TrackerUpdateAction
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

class BetterModelEntity(
    player: Player,
    modelId: Var<String>,
    val defaultAnimationSettings: DefaultAnimationSettings,
    hitboxWidth: Double = 1.0,
    hitboxHeight: Double = 2.0,
) : FakeEntity(player) {

    val modelId: String = modelId.get(player)

    private val fakeEntityId: Int = SpigotReflectionUtil.generateEntityId()
    private var tracker: DummyTracker? = null
    private val disposed = AtomicBoolean(false)
    private val hitbox = InteractionEntity(player, hitboxWidth, hitboxHeight)

    override val entityId: Int
        get() = fakeEntityId

    override val state: EntityState
        get() = EntityState(height(), speed())

    override fun applyProperties(properties: List<EntityProperty>) {
        val tracker = tracker ?: return
        properties.forEach { property ->
            when (property) {
                is PositionProperty -> {
                    tracker.location(BukkitAdapter.adapt(property.toBukkitLocation()))
                    hitbox.consumeProperties(property)
                }

                is ScaleProperty -> {
                    tracker.scaler(ModelScaler.value(property.scale.toFloat()))
                }

                is InvisibleProperty -> {
                    val platformPlayer = BukkitAdapter.adapt(player)
                    if (property.invisible) tracker.remove(platformPlayer)
                    else tracker.spawn(platformPlayer)
                }

                is GlowingEffectProperty -> {
                    tracker.update(
                        TrackerUpdateAction.composite(
                            TrackerUpdateAction.glow(property.glowing),
                            TrackerUpdateAction.glowColor(property.color.color.toInt())
                        )
                    )
                }

                else -> {
                }
            }
        }
    }

    override fun tick() {
        hitbox.tick()
    }

    override fun spawn(location: PositionProperty) {
        if (modelId.isEmpty() || disposed.get()) return
        val renderer = BetterModel.modelOrNull(modelId) ?: return

        hitbox.spawn(location)

        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (disposed.get()) return@Runnable
            val tracker = renderer.create(BukkitAdapter.adapt(location.toBukkitLocation()))
            this.tracker = tracker
            tracker.spawn(BukkitAdapter.adapt(player))
            activeEntities[fakeEntityId] = this
        })

        super.spawn(location)
    }

    override fun dispose() {
        if (disposed.getAndSet(true)) return
        hitbox.dispose()
        Bukkit.getScheduler().runTask(plugin, Runnable {
            activeEntities.remove(fakeEntityId, this)
            tracker?.close()
            tracker = null
        })
    }

    override fun addPassenger(entity: FakeEntity) {
        if (entity.entityId == entityId) return
    }

    override fun removePassenger(entity: FakeEntity) {
        if (entity.entityId == entityId) return
    }

    override fun contains(entityId: Int): Boolean {
        if (this.entityId == entityId) return true
        if (hitbox.contains(entityId)) return true
        return false
    }

    fun height(): Double = tracker?.height() ?: 0.0

    fun speed(): Float = property(SpeedProperty::class)?.speed ?: 0.2085f

    fun tracker(): DummyTracker? = tracker

    companion object {
        val activeEntities: MutableMap<Int, BetterModelEntity> = ConcurrentHashMap()
    }
}
