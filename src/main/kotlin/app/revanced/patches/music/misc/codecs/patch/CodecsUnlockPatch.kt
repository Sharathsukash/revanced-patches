package app.revanced.patches.music.misc.codecs.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.music.utils.annotations.MusicCompatibility
import app.revanced.patches.music.utils.settings.resource.patch.SettingsPatch
import app.revanced.patches.shared.patch.opus.AbstractOpusCodecsPatch
import app.revanced.util.enum.CategoryType
import app.revanced.util.integrations.Constants.MUSIC_MISC_PATH

@Patch
@Name("Enable opus codec")
@Description("Enable opus codec when playing audio.")
@DependsOn([SettingsPatch::class])
@MusicCompatibility
@Version("0.0.1")
class CodecsUnlockPatch : AbstractOpusCodecsPatch(
    "$MUSIC_MISC_PATH/OpusCodecPatch;->enableOpusCodec()Z"
) {
    override fun execute(context: BytecodeContext): PatchResult {
        super.execute(context)

        SettingsPatch.addMusicPreference(
            CategoryType.MISC,
            "revanced_enable_opus_codec",
            "true"
        )

        return PatchResultSuccess()
    }
}
