package app.revanced.patches.youtube.flyoutpanel.oldspeedlayout.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags

object FlyoutPanelPatchFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.PRIVATE or AccessFlags.STATIC,
    parameters = emptyList(),
    customFingerprint = { methodDef, _ -> methodDef.definingClass.endsWith("/FlyoutPanelPatch;") && methodDef.name == "openOldPlaybackRateBottomSheetFragment" }
)