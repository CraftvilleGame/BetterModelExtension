package entries.entity.instance

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.entries.Ref
import com.typewritermc.core.entries.emptyRef
import com.typewritermc.core.extension.annotations.Entry
import com.typewritermc.core.extension.annotations.OnlyTags
import com.typewritermc.core.extension.annotations.Tags
import com.typewritermc.core.utils.point.Position
import com.typewritermc.engine.paper.entry.entity.SimpleEntityInstance
import com.typewritermc.engine.paper.entry.entries.EntityData
import com.typewritermc.engine.paper.entry.entries.SharedEntityActivityEntry
import entries.entity.definition.BetterModelDefinition

@Tags("bettermodel_instance")
@Entry(
    "bettermodel_instance",
    "An instance of a simplified premade BetterModel entity",
    Colors.YELLOW,
    "material-symbols:account-box"
)
class BetterModelInstance(
    override val id: String = "",
    override val name: String = "",
    override val definition: Ref<BetterModelDefinition> = emptyRef(),
    override val spawnLocation: Position = Position.ORIGIN,
    @OnlyTags("generic_entity_data", "living_entity_data", "lines", "player_data")
    override val data: List<Ref<EntityData<*>>> = emptyList(),
    override val activity: Ref<out SharedEntityActivityEntry> = emptyRef(),
) : SimpleEntityInstance
