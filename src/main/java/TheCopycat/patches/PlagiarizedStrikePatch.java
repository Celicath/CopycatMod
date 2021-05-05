package TheCopycat.patches;

import TheCopycat.cards.PlagiarizedStrike;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

public class PlagiarizedStrikePatch {
	public static boolean portraitUpgraded = false;

	@SpirePatch(clz = SingleCardViewPopup.class, method = "renderPortrait")
	public static class HandCardSelectPatch {
		@SpirePrefixPatch
		public static void Prefix(SingleCardViewPopup __instance, SpriteBatch sb, AbstractCard ___card, @ByRef Texture[] ___portraitImg) {
			if (___card instanceof PlagiarizedStrike && SingleCardViewPopup.isViewingUpgrade != portraitUpgraded) {
				___portraitImg[0].dispose();
				___portraitImg[0] = CustomCard.getPortraitImage((CustomCard) ___card);
			}
		}
	}
}
