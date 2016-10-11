package com.example.answer.bean;

public class SaveQuestionInfo {
	
	private String questionId;//题目id
	private String is_correct;//是否正确
	private int score;//分值
	
	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getIs_correct() {
		return is_correct;
	}

	public void setIs_correct(String is_correct) {
		this.is_correct = is_correct;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "SaveQuestionInfo{" +
				"questionId='" + questionId + '\'' +
				", is_correct='" + is_correct + '\'' +
				", score='" + score + '\'' +
				'}';
	}
}
