package br.net.christiano322.PlayMoreSounds.api.events.options;

public interface PeriodCancelled {
	public void setCancelledThisRound(boolean value);

	public boolean isCancelledThisRound();
}
