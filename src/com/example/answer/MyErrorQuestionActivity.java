package com.example.answer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.answer.adapter.MyErrorQuestionListAdapter;
import com.example.answer.bean.ErrorQuestion;
import com.example.answer.bean.ErrorQuestionInfo;
import com.example.answer.database.DBManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 我的错题
 * 
 * @author 金钟焕
 */
public class MyErrorQuestionActivity extends Activity {

	private ImageView left;
	private TextView title;

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();// 列表数据
	private ListView listView;
	
	private List<ErrorQuestion> list=new ArrayList<ErrorQuestion>();

	private MyErrorQuestionListAdapter adapter;
	
	ErrorQuestion question;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.my_error_question);

		initView();
	}

	private void initView() {
		left = (ImageView) findViewById(R.id.left);
		title = (TextView) findViewById(R.id.title);
		title.setText("我的错题");
		listView = (ListView) findViewById(R.id.listview);

		left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		adapter = new MyErrorQuestionListAdapter(this, data, listView);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MyErrorQuestionActivity.this,MyErrorQuestionDetailActivity.class);
				question=list.get(position);
				intent.putExtra("topic", question.getQuestionName());
				intent.putExtra("questionAnswer", question.getCorrectanswer());
				intent.putExtra("questionSelect", question.getYouranswer());
				intent.putExtra("optionA", question.getOptionA());
				intent.putExtra("optionB", question.getOptionB());
				intent.putExtra("optionC", question.getOptionC());
				intent.putExtra("optionD", question.getOptionD());
				startActivity(intent);
			}
		});
		
		DBManager dbManager = new DBManager(MyErrorQuestionActivity.this);
		dbManager.openDB();

		ErrorQuestionInfo[] errorQuestionInfos = dbManager.queryAllData();
		if (errorQuestionInfos == null) {
			Toast.makeText(MyErrorQuestionActivity.this, "暂无数据",
					Toast.LENGTH_SHORT).show();
		} else {
			Map<String, Object> map = null;
			for (int i = 0; i < errorQuestionInfos.length; i++) {
				ErrorQuestion errorQuestion=new ErrorQuestion();
				map = new HashMap<String, Object>();
				map.put("title", errorQuestionInfos[i].topic);// 标题
				map.put("type", errorQuestionInfos[i].correctAnswer.length()==1?"选择题":"多选题");// 标题
				map.put("answer", errorQuestionInfos[i].correctAnswer);// 标题
				map.put("selected", errorQuestionInfos[i].yourAnswer);//
				data.add(map);
				
				errorQuestion.setQuestionName(errorQuestionInfos[i].topic);
				errorQuestion.setCorrectanswer(errorQuestionInfos[i].correctAnswer);
				errorQuestion.setYouranswer(errorQuestionInfos[i].yourAnswer);
				errorQuestion.setOptionA(errorQuestionInfos[i].optionA);
				errorQuestion.setOptionB(errorQuestionInfos[i].optionB);
				errorQuestion.setOptionC(errorQuestionInfos[i].optionC);
				errorQuestion.setOptionD(errorQuestionInfos[i].optionD);
				list.add(errorQuestion);
			}
		}

	}

}
