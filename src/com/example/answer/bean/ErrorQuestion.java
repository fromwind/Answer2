package com.example.answer.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ErrorQuestion implements Parcelable{
	
	public int questionId;
	public String questionName;
	public String correctanswer;
	public String youranswer;
	public String optionA;
	public String optionB;
	public String optionC;
	public String optionD;
public ErrorQuestion(){}
	protected ErrorQuestion(Parcel in) {
		questionId = in.readInt();
		questionName = in.readString();
		correctanswer = in.readString();
		youranswer = in.readString();
		optionA = in.readString();
		optionB = in.readString();
		optionC = in.readString();
		optionD = in.readString();
	}

	public static final Creator<ErrorQuestion> CREATOR = new Creator<ErrorQuestion>() {
		@Override
		public ErrorQuestion createFromParcel(Parcel in) {
			return new ErrorQuestion(in);
		}

		@Override
		public ErrorQuestion[] newArray(int size) {
			return new ErrorQuestion[size];
		}
	};

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public String getQuestionName() {
		return questionName;
	}

	public void setQuestionName(String questionName) {
		this.questionName = questionName;
	}

	public String getCorrectanswer() {
		return correctanswer;
	}

	public void setCorrectanswer(String correctanswer) {
		this.correctanswer = correctanswer;
	}

	public String getYouranswer() {
		return youranswer;
	}

	public void setYouranswer(String youranswer) {
		this.youranswer = youranswer;
	}

	public String getOptionA() {
		return optionA;
	}

	public void setOptionA(String optionA) {
		this.optionA = optionA;
	}

	public String getOptionB() {
		return optionB;
	}

	public void setOptionB(String optionB) {
		this.optionB = optionB;
	}

	public String getOptionC() {
		return optionC;
	}

	public void setOptionC(String optionC) {
		this.optionC = optionC;
	}

	public String getOptionD() {
		return optionD;
	}

	public void setOptionD(String optionD) {
		this.optionD = optionD;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		// TODO Auto-generated method stub
		dest.writeString(questionName);
		dest.writeString(correctanswer);
		dest.writeString(youranswer);
		dest.writeString(optionA);
		dest.writeString(optionB);
		dest.writeString(optionC);
		dest.writeString(optionD);
	}
	
	

}
