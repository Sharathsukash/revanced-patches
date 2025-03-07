package app.revanced.patches.youtube.misc.externalbrowser.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

object ExternalBrowserSecondaryFingerprint : MethodFingerprint(
    returnType = "L",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    opcodes = listOf(
        Opcode.IPUT_OBJECT,
        Opcode.NEW_INSTANCE,
        Opcode.CONST_STRING
    ),
    strings = listOf("android.support.customtabs.action.CustomTabsService")
)