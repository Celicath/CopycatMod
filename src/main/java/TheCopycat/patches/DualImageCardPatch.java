package TheCopycat.patches;

import TheCopycat.interfaces.DualImageCard;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

public class DualImageCardPatch {
	public static boolean portraitUpgraded = false;

	@SpirePatch2(clz = SingleCardViewPopup.class, method = "renderPortrait")
	public static class HandCardSelectPatch {
		@SpirePrefixPatch
		public static void Prefix(AbstractCard ___card, @ByRef Texture[] ___portraitImg) {
			if (___card instanceof DualImageCard && ((DualImageCard) ___card).viewUpgradedImage() != portraitUpgraded) {
				___portraitImg[0].dispose();
				___portraitImg[0] = CustomCard.getPortraitImage((CustomCard) ___card);
			}
		}
	}
}
