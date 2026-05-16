package entries.action

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.entries.Ref
import com.typewritermc.core.entries.emptyRef
import com.typewritermc.core.extension.annotations.Entry
import com.typewritermc.core.extension.annotations.Help
import com.typewritermc.engine.paper.entry.AudienceManager
import com.typewritermc.engine.paper.entry.Criteria
import com.typewritermc.engine.paper.entry.Modifier
import com.typewritermc.engine.paper.entry.TriggerableEntry
import com.typewritermc.engine.paper.entry.entity.AudienceEntityDisplay
import com.typewritermc.engine.paper.entry.entries.ActionEntry
import com.typewritermc.engine.paper.entry.entries.ActionTrigger
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.entry.entries.Var
import entries.entity.BetterModelEntity
import entries.entity.instance.BetterModelInstance
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Entry("stop_bettermodel_animation", "Stop a BetterModel animation.", Colors.RED, "material-symbols:touch-app-rounded")
class StopAnimationEntry(
    override val id: String = "",
    override val name: String = "",
    override val criteria: List<Criteria> = emptyList(),
    override val modifiers: List<Modifier> = emptyList(),
    override val triggers: List<Ref<TriggerableEntry>> = emptyList(),
    @Help("The BetterModel entity to stop the animation on.")
    val entity: Ref<BetterModelInstance> = emptyRef(),
    @Help("The name of the animation to stop.")
    val animation: Var<String> = ConstVar("idle"),
) : ActionEntry, KoinComponent {

    private val audienceManager: AudienceManager by inject()

    override fun ActionTrigger.execute() {
        val display = audienceManager[entity] as? AudienceEntityDisplay ?: return
        val entityId = display.entityId(player.uniqueId)
        val betterModelEntity = BetterModelEntity.activeEntities[entityId] ?: return
        val tracker = betterModelEntity.tracker() ?: return

        val name = animation.get(player)
        if (name.isNotEmpty()) tracker.stopAnimation(name)
    }
}
