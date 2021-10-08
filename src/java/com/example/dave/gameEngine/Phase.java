package com.example.dave.gameEngine;

interface Phase{
	boolean isOn();
}

enum GamePhase implements Phase {
	Play {
		@Override public boolean isOn() {
			return true;
		}
	}, StoryAnte {
		@Override public boolean isOn() {
			return true;
		}
	}, StoryPost {
		@Override public boolean isOn() {
			return true;
		}
	}, End {
		@Override public boolean isOn() {
			return false;
		}
	}, Credits {
		@Override public boolean isOn() {
			return true;
		}
	};
}

enum SectionPhase implements Phase{
	Win {
		@Override public boolean isOn() {
			return false;
		}
	}, Lose {
		@Override public boolean isOn() {
			return false;
		}
	}, Play {
		@Override public boolean isOn() {
			return true;
		}
	};
}