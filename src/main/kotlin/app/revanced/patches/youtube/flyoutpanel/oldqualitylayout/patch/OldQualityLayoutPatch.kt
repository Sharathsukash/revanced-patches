package app.revanced.patches.youtube.flyoutpanel.oldqualitylayout.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.flyoutpanel.oldqualitylayout.fingerprints.QualityMenuViewInflateFingerprint
import app.revanced.patches.youtube.utils.annotations.YouTubeCompatibility
import app.revanced.patches.youtube.utils.fingerprints.NewFlyoutPanelBuilderFingerprint
import app.revanced.patches.youtube.utils.litho.patch.LithoFilterPatch
import app.revanced.patches.youtube.utils.resourceid.patch.SharedResourceIdPatch
import app.revanced.patches.youtube.utils.settings.resource.patch.SettingsPatch
import app.revanced.util.integrations.Constants.FLYOUT_PANEL
import app.revanced.util.integrations.Constants.PATCHES_PATH
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Patch
@Name("Enable old quality layout")
@Description("Enables the original quality flyout menu.")
@DependsOn(
    [
        LithoFilterPatch::class,
        SettingsPatch::class,
        SharedResourceIdPatch::class
    ]
)
@YouTubeCompatibility
@Version("0.0.1")
class OldQualityLayoutPatch : BytecodePatch(
    listOf(
        NewFlyoutPanelBuilderFingerprint,
        QualityMenuViewInflateFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {

        /**
         * Old method
         */
        QualityMenuViewInflateFingerprint.result?.let {
            it.mutableMethod.apply {
                val insertIndex = it.scanResult.patternScanResult!!.endIndex
                val insertRegister = getInstruction<OneRegisterInstruction>(insertIndex).registerA

                addInstruction(
                    insertIndex + 1,
                    "invoke-static { v$insertRegister }, $FLYOUT_PANEL->enableOldQualityMenu(Landroid/widget/ListView;)V"
                )
            }
        } ?: return QualityMenuViewInflateFingerprint.toErrorResult()

        /**
         * New method
         */
        NewFlyoutPanelBuilderFingerprint.result?.let {
            it.mutableMethod.apply {
                val insertIndex = implementation!!.instructions.size - 1
                val insertRegister = getInstruction<OneRegisterInstruction>(insertIndex).registerA

                addInstruction(
                    insertIndex,
                    "invoke-static { v$insertRegister }, $FLYOUT_PANEL->enableOldQualityMenu(Landroid/widget/LinearLayout;)V"
                )
            }
        } ?: return NewFlyoutPanelBuilderFingerprint.toErrorResult()

        LithoFilterPatch.addFilter("$PATCHES_PATH/ads/VideoQualityMenuFilter;")

        /**
         * Add settings
         */
        SettingsPatch.addPreference(
            arrayOf(
                "PREFERENCE: FLYOUT_PANEL_SETTINGS",
                "SETTINGS: PLAYER_FLYOUT_PANEL_HEADER",
                "SETTINGS: ENABLE_OLD_QUALITY_LAYOUT"
            )
        )

        SettingsPatch.updatePatchStatus("enable-old-quality-layout")

        return PatchResultSuccess()
    }
}
