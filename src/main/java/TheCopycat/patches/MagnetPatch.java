package TheCopycat.patches;

import TheCopycat.actions.MagnetAction;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class MagnetPatch {
	@SpirePatch2(clz = AbstractPower.class, method = "renderIcons")
	@SpirePatch2(clz = AbstractPower.class, method = "renderAmount")
	public static class DiscardPatch {
		@SpirePrefixPatch
		public static void Prefix(AbstractPower __instance, @ByRef float[] x, @ByRef float[] y) {
			if (__instance == MagnetAction.pow) {
				if (MagnetAction.targetDX == 0 && MagnetAction.targetDY == 0) {
					MagnetAction.calcTargetDelta(x[0], y[0]);
				} else {
					x[0] += MagnetAction.targetDX * MagnetAction.t;
					y[0] += MagnetAction.targetDY * MagnetAction.t;
				}
			}
		}
	}
}
