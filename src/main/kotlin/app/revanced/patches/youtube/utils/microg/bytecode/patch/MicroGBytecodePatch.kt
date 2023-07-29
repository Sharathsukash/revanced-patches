package app.revanced.patches.youtube.utils.microg.bytecode.patch

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.shared.patch.packagename.PackageNamePatch
import app.revanced.patches.youtube.utils.fix.clientspoof.patch.ClientSpoofPatch
import app.revanced.patches.youtube.utils.fix.parameter.patch.SpoofPlayerParameterPatch
import app.revanced.patches.youtube.utils.microg.bytecode.fingerprints.CastContextFetchFingerprint
import app.revanced.patches.youtube.utils.microg.bytecode.fingerprints.CastDynamiteModuleFingerprint
import app.revanced.patches.youtube.utils.microg.bytecode.fingerprints.CastDynamiteModuleV2Fingerprint
import app.revanced.patches.youtube.utils.microg.bytecode.fingerprints.GooglePlayUtilityFingerprint
import app.revanced.patches.youtube.utils.microg.bytecode.fingerprints.PrimeFingerprint
import app.revanced.patches.youtube.utils.microg.bytecode.fingerprints.ServiceCheckFingerprint
import app.revanced.patches.youtube.utils.microg.shared.Constants.PACKAGE_NAME
import app.revanced.util.bytecode.BytecodeHelper.injectInit
import app.revanced.util.microg.MicroGBytecodeHelper

@DependsOn(
    [
        ClientSpoofPatch::class,
        PackageNamePatch::class,
        SpoofPlayerParameterPatch::class
    ]
)
class MicroGBytecodePatch : BytecodePatch(
    listOf(
        CastContextFetchFingerprint,
        CastDynamiteModuleFingerprint,
        CastDynamiteModuleV2Fingerprint,
        GooglePlayUtilityFingerprint,
        PrimeFingerprint,
        ServiceCheckFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {

        val packageName = PackageNamePatch.YouTubePackageName
            ?: throw PatchResultError("Invalid package name.")

        if (packageName == PACKAGE_NAME)
            throw PatchResultError("Original package name is not available as package name for MicroG build.")

        // apply common microG patch
        MicroGBytecodeHelper.patchBytecode(
            context, arrayOf(
                MicroGBytecodeHelper.packageNameTransform(
                    PACKAGE_NAME,
                    packageName
                )
            ),
            MicroGBytecodeHelper.PrimeMethodTransformationData(
                PrimeFingerprint,
                PACKAGE_NAME,
                packageName
            ),
            listOf(
                ServiceCheckFingerprint,
                GooglePlayUtilityFingerprint,
                CastDynamiteModuleFingerprint,
                CastDynamiteModuleV2Fingerprint,
                CastContextFetchFingerprint
            )
        )

        context.injectInit("MicroGPatch", "checkAvailability")

        return PatchResultSuccess()
    }
}
