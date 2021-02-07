package TheCopycat.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.CardLibrary;

public class CharacterEnum {
	public static class CardColorEnum {
		@SpireEnum
		public static AbstractCard.CardColor COPYCAT_BLUE;
		@SpireEnum
		public static AbstractCard.CardColor COPYCAT_MONSTER;
	}

	public static class LibraryEnum {
		@SpireEnum
		public static CardLibrary.LibraryType COPYCAT_BLUE;
		@SpireEnum
		public static CardLibrary.LibraryType COPYCAT_MONSTER;
	}

	public static class PlayerClassEnum {
		@SpireEnum
		public static AbstractPlayer.PlayerClass THE_COPYCAT;
	}

	public static class CustomTags {
		@SpireEnum
		public static AbstractCard.CardTags COPYCAT_MIMIC;
	}
}
