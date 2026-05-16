package entries.entity.definition

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.entries.Ref
import com.typewritermc.core.entries.ref
import com.typewritermc.core.extension.annotations.*
import com.typewritermc.engine.paper.entry.entity.FakeEntity
import com.typewritermc.engine.paper.entry.entity.SimpleEntityDefinition
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.entry.entries.EntityData
import com.typewritermc.engine.paper.entry.entries.Var
import com.typewritermc.engine.paper.utils.Sound
import entries.entity.BetterModelEntity
import entries.entity.NamedBetterModelEntity
import org.bukkit.entity.Player

@Entry(
    "bettermodel_definition",
    "A simplified premade BetterModel entity",
    Colors.ORANGE,
    "material-symbols:account-box"
)
@Tags("bettermodel_definition")
class BetterModelDefinition(
    override val id: String = "",
    override val name: String = "",
    override val displayName: Var<String> = ConstVar(""),
    override val sound: Var<Sound> = ConstVar(Sound.EMPTY),
    @OnlyTags("generic_entity_data", "living_entity_data", "lines", "player_data")
    override val data: List<Ref<EntityData<*>>> = emptyList(),
    @Help("Set the model of the entity")
    val modelId: Var<String> = ConstVar(""),
    @Help("Whether the entity is named or not, showing a nametag.")
    val named: Var<Boolean> = ConstVar(false),
    @Help("Configure if you want default animations to be used.")
    val defaultAnimationSettings: DefaultAnimationSettings = DefaultAnimationSettings(),
    @Help("Width of the clickable hitbox covering the model (in blocks).")
    @Default("1.0")
    val hitboxWidth: Double = 1.0,
    @Help("Height of the clickable hitbox covering the model (in blocks).")
    @Default("2.0")
    val hitboxHeight: Double = 2.0,
) : SimpleEntityDefinition {

    override fun create(player: Player): FakeEntity {
        if (named.get(player)) return NamedBetterModelEntity(
            player,
            displayName,
            modelId,
            defaultAnimationSettings,
            ref(),
            hitboxWidth,
            hitboxHeight,
        )
        return BetterModelEntity(player, modelId, defaultAnimationSettings, hitboxWidth, hitboxHeight)
    }
}

data class DefaultAnimationSettings(
    @Help("Run the default walk animation if the entity is moving") @Default("true")
    val walkAnimation: Boolean = true,
)
