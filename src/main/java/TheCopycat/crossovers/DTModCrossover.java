package TheCopycat.crossovers;

import TheDT.characters.DragonTamer;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DTModCrossover {
	public static AbstractPlayer getLivingDragon() {
		return DragonTamer.getLivingDragon();
	}

	public static AbstractCreature getCurrentTarget(AbstractMonster m) {
		return DragonTamer.getCurrentTarget(m);
	}
}
