package TheCopycat.patches;

import TheCopycat.potions.SlimePotion;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.vfx.FlashPotionEffect;

public class SlimePotionPatch {
	@SpirePatch2(clz = AbstractPotion.class, method = "initializeImage")
	public static class InitImagePatch {
		@SpirePrefixPatch
		public static SpireReturn<Void> Prefix(AbstractPotion __instance, @ByRef Texture[] ___containerImg, @ByRef Texture[] ___liquidImg, @ByRef Texture[] ___hybridImg, @ByRef Texture[] ___spotsImg, @ByRef Texture[] ___outlineImg) {
			if (__instance instanceof SlimePotion) {
				___containerImg[0] = SlimePotion.CONTAINER;
				___liquidImg[0] = SlimePotion.LIQUID;
				___hybridImg[0] = SlimePotion.HYBRID;
				___spotsImg[0] = SlimePotion.SPOTS;
				___outlineImg[0] = SlimePotion.OUTLINE;
				return SpireReturn.Return();
			} else {
				return SpireReturn.Continue();
			}
		}
	}

	@SpirePatch2(clz = FlashPotionEffect.class, method = SpirePatch.CONSTRUCTOR)
	public static class PotionEffectPatch {
		@SpirePostfixPatch
		public static void Postfix(AbstractPotion p, @ByRef Texture[] ___containerImg, @ByRef Texture[] ___liquidImg, @ByRef Texture[] ___hybridImg, @ByRef Texture[] ___spotsImg) {
			if (p instanceof SlimePotion) {
				___containerImg[0] = SlimePotion.CONTAINER;
				___liquidImg[0] = SlimePotion.LIQUID;
				___hybridImg[0] = SlimePotion.HYBRID;
				___spotsImg[0] = SlimePotion.SPOTS;
			}
		}
	}
}
