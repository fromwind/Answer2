package com.example.answer.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.answer.AnalogyExaminationActivity;
import com.example.answer.MyErrorQuestionActivity;
import com.example.answer.R;
import com.example.answer.bean.ErrorQuestionInfo;
import com.example.answer.bean.Question;
import com.example.answer.bean.SaveQuestionInfo;
import com.example.answer.database.DBManager;
import com.example.answer.util.ConstantUtil;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ExaminationSubmitAdapter extends PagerAdapter {

    AnalogyExaminationActivity mContext;
    // 传递过来的页面view的集合
    List<View> viewItems;
    // 每个item的页面view
    View convertView;
    // 传递过来的所有数据
    List<Question> dataItems;

    String imgServerUrl = "";

    private Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
    private Map<Integer, Boolean> mapClick = new HashMap<Integer, Boolean>();
    private Map<Integer, String> mapMultiSelect = new HashMap<Integer, String>();

    boolean isClick = false;

    boolean isNext = false;

    StringBuffer answer = new StringBuffer();
    StringBuffer answerLast = new StringBuffer();
    StringBuffer answer1 = new StringBuffer();

    DBManager dbManager;

    String isCorrect = ConstantUtil.isCorrect;//1对，0错

    int errortopicNum = 0;

    String resultA = "";
    String resultB = "";
    String resultC = "";
    String resultD = "";

    public ExaminationSubmitAdapter(AnalogyExaminationActivity context, List<View> viewItems, List<Question>
            dataItems, String imgServerUrl) {
        mContext = context;
        this.viewItems = viewItems;
        this.dataItems = dataItems;
        this.imgServerUrl = imgServerUrl;
        dbManager = new DBManager(context);
        dbManager.openDB();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewItems.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final ViewHolder holder = new ViewHolder();
        convertView = viewItems.get(position);
        holder.questionType = (TextView) convertView.findViewById(R.id.activity_prepare_test_no);
        holder.question = (TextView) convertView.findViewById(R.id.activity_prepare_test_question);
        holder.previousBtn = (LinearLayout) convertView.findViewById(R.id.activity_prepare_test_upLayout);
        holder.nextBtn = (LinearLayout) convertView.findViewById(R.id.activity_prepare_test_nextLayout);
        holder.nextText = (TextView) convertView.findViewById(R.id.menu_bottom_nextTV);
        holder.errorBtn = (LinearLayout) convertView.findViewById(R.id.activity_prepare_test_errorLayout);
        holder.totalText = (TextView) convertView.findViewById(R.id.activity_prepare_test_totalTv);
        holder.nextImage = (ImageView) convertView.findViewById(R.id.menu_bottom_nextIV);
        holder.wrongLayout = (LinearLayout) convertView.findViewById(R.id.activity_prepare_test_wrongLayout);
        holder.explaindetailTv = (TextView) convertView.findViewById(R.id.activity_prepare_test_explaindetail);
        holder.layoutA = (LinearLayout) convertView.findViewById(R.id.activity_prepare_test_layout_a);
        holder.layoutB = (LinearLayout) convertView.findViewById(R.id.activity_prepare_test_layout_b);
        holder.layoutC = (LinearLayout) convertView.findViewById(R.id.activity_prepare_test_layout_c);
        holder.layoutD = (LinearLayout) convertView.findViewById(R.id.activity_prepare_test_layout_d);
        holder.ivA = (ImageView) convertView.findViewById(R.id.vote_submit_select_image_a);
        holder.ivB = (ImageView) convertView.findViewById(R.id.vote_submit_select_image_b);
        holder.ivC = (ImageView) convertView.findViewById(R.id.vote_submit_select_image_c);
        holder.ivD = (ImageView) convertView.findViewById(R.id.vote_submit_select_image_d);
        holder.tvA = (TextView) convertView.findViewById(R.id.vote_submit_select_text_a);
        holder.tvB = (TextView) convertView.findViewById(R.id.vote_submit_select_text_b);
        holder.tvC = (TextView) convertView.findViewById(R.id.vote_submit_select_text_c);
        holder.tvD = (TextView) convertView.findViewById(R.id.vote_submit_select_text_d);

        holder.totalText.setText(position + 1 + "/" + dataItems.size());

        holder.errorBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(mContext, MyErrorQuestionActivity.class);
                mContext.startActivity(intent);
            }
        });

        if (dataItems.get(position).getOptionC()==null) {
            holder.layoutC.setVisibility(View.GONE);
        }
        if (dataItems.get(position).getOptionD()==null) {
            holder.layoutD.setVisibility(View.GONE);
        }

        //判断是否文字图片题目
        //文字题目
        holder.tvA.setVisibility(View.VISIBLE);
        holder.tvB.setVisibility(View.VISIBLE);
        holder.tvC.setVisibility(View.VISIBLE);
        holder.tvD.setVisibility(View.VISIBLE);
        holder.tvA.setText("A." + dataItems.get(position).getOptionA());
        holder.tvB.setText("B." + dataItems.get(position).getOptionB());
        holder.tvC.setText("C." + dataItems.get(position).getOptionC());
        holder.tvD.setText("D." + dataItems.get(position).getOptionD());

        if (dataItems.get(position).getCorrectAnswer().length()==1) {
            //单选题
            holder.question.setText("(单选题)" + dataItems.get(position).getTopic());

            holder.layoutA.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (map.containsKey(position)) {
                        return;
                    }
                    map.put(position, true);

                    if (dataItems.get(position).getCorrectAnswer().contains("A")) {
                        mContext.setCurrentView(position + 1);
                        holder.ivA.setImageResource(R.drawable.ic_practice_test_right);
                        holder.tvA.setTextColor(Color.parseColor("#61bc31"));
                        isCorrect = ConstantUtil.isCorrect;
                    } else {
                        isCorrect = ConstantUtil.isError;
                        errortopicNum += 1;
                        //自动添加错误题目
                        ErrorQuestionInfo errorQuestionInfo = new ErrorQuestionInfo();
                        errorQuestionInfo.setTopic(dataItems.get(position).getTopic());
                        errorQuestionInfo.setCorrectAnswer(dataItems.get(position).getCorrectAnswer());
                        errorQuestionInfo.setYourAnswer("A");
                        errorQuestionInfo.setOptionA(dataItems.get(position).getOptionA());
                        errorQuestionInfo.setOptionB(dataItems.get(position).getOptionB());
                        errorQuestionInfo.setOptionC(dataItems.get(position).getOptionC());
                        errorQuestionInfo.setOptionD(dataItems.get(position).getOptionD());

                        long colunm = dbManager.insertErrorQuestion(errorQuestionInfo);

                        if (colunm == -1) {
                            Toast.makeText(mContext, "添加错误", Toast.LENGTH_SHORT).show();
                        }

                        holder.ivA.setImageResource(R.drawable.ic_practice_test_wrong);
                        holder.tvA.setTextColor(Color.parseColor("#d53235"));
                        //提示
                        holder.wrongLayout.setVisibility(View.VISIBLE);
                        //显示正确选项
                        if (dataItems.get(position).getCorrectAnswer().contains("A")) {
                            holder.ivA.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvA.setTextColor(Color.parseColor("#61bc31"));
                        } else if (dataItems.get(position).getCorrectAnswer().contains("B")) {
                            holder.ivB.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvB.setTextColor(Color.parseColor("#61bc31"));
                        } else if (dataItems.get(position).getCorrectAnswer().contains("C")) {
                            holder.ivC.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvC.setTextColor(Color.parseColor("#61bc31"));
                        } else if (dataItems.get(position).getCorrectAnswer().contains("D")) {
                            holder.ivD.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvD.setTextColor(Color.parseColor("#61bc31"));
                        }
                    }
                    //保存数据
                    SaveQuestionInfo questionInfo = new SaveQuestionInfo();
                    questionInfo.setQuestionId(dataItems.get(position).getTopicId());
                    dataItems.get(position).getTopicId();
                    questionInfo.setScore(2);
                    questionInfo.setIs_correct(isCorrect);
                    mContext.questionInfos.add(questionInfo);
                    dataItems.get(position).setYourAnswer("0");
                }
            });
            holder.layoutB.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (map.containsKey(position)) {
                        return;
                    }
                    map.put(position, true);
                    if (dataItems.get(position).getCorrectAnswer().contains("B")) {
                        mContext.setCurrentView(position + 1);
                        holder.ivB.setImageResource(R.drawable.ic_practice_test_right);
                        holder.tvB.setTextColor(Color.parseColor("#61bc31"));
                        isCorrect = ConstantUtil.isCorrect;
                    } else {
                        isCorrect = ConstantUtil.isError;
                        errortopicNum += 1;
                        //自动添加错误题目
                        ErrorQuestionInfo errorQuestionInfo = new ErrorQuestionInfo();
                        errorQuestionInfo.setTopic(dataItems.get(position).getTopic());
                        errorQuestionInfo.setCorrectAnswer(dataItems.get(position).getCorrectAnswer());
                        errorQuestionInfo.setYourAnswer("B");
                        errorQuestionInfo.setOptionA(dataItems.get(position).getOptionA());
                        errorQuestionInfo.setOptionB(dataItems.get(position).getOptionB());
                        errorQuestionInfo.setOptionC(dataItems.get(position).getOptionC());
                        errorQuestionInfo.setOptionD(dataItems.get(position).getOptionD());

                        long colunm = dbManager.insertErrorQuestion(errorQuestionInfo);

                        if (colunm == -1) {
                            Toast.makeText(mContext, "添加错误", Toast.LENGTH_SHORT).show();
                        }
                        holder.ivB.setImageResource(R.drawable.ic_practice_test_wrong);
                        holder.tvB.setTextColor(Color.parseColor("#d53235"));
                        //提示
                        holder.wrongLayout.setVisibility(View.VISIBLE);
                        //显示正确选项
                        if (dataItems.get(position).getCorrectAnswer().contains("A")) {
                            holder.ivA.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvA.setTextColor(Color.parseColor("#61bc31"));
                        } else if (dataItems.get(position).getCorrectAnswer().contains("B")) {
                            holder.ivB.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvB.setTextColor(Color.parseColor("#61bc31"));
                        } else if (dataItems.get(position).getCorrectAnswer().contains("C")) {
                            holder.ivC.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvC.setTextColor(Color.parseColor("#61bc31"));
                        } else if (dataItems.get(position).getCorrectAnswer().contains("D")) {
                            holder.ivD.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvD.setTextColor(Color.parseColor("#61bc31"));
                        }
                    }
                    //保存数据
                    SaveQuestionInfo questionInfo = new SaveQuestionInfo();
                    questionInfo.setQuestionId(dataItems.get(position).getTopicId());
                    questionInfo.setScore(2);
                    questionInfo.setIs_correct(isCorrect);
                    mContext.questionInfos.add(questionInfo);
                    dataItems.get(position).setYourAnswer("0");
                }
            });
            holder.layoutC.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (map.containsKey(position)) {
                        return;
                    }
                    map.put(position, true);
                    if (dataItems.get(position).getCorrectAnswer().contains("C")) {
                        mContext.setCurrentView(position + 1);
                        holder.ivC.setImageResource(R.drawable.ic_practice_test_right);
                        holder.tvC.setTextColor(Color.parseColor("#61bc31"));
                        isCorrect = ConstantUtil.isCorrect;
                    } else {
                        isCorrect = ConstantUtil.isError;
                        errortopicNum += 1;
                        //自动添加错误题目
                        ErrorQuestionInfo errorQuestionInfo = new ErrorQuestionInfo();
                        errorQuestionInfo.setTopic(dataItems.get(position).getTopic());
                        errorQuestionInfo.setCorrectAnswer(dataItems.get(position).getCorrectAnswer());

                        errorQuestionInfo.setYourAnswer("C");

                        errorQuestionInfo.setOptionA(dataItems.get(position).getOptionA());
                        errorQuestionInfo.setOptionB(dataItems.get(position).getOptionB());
                        errorQuestionInfo.setOptionC(dataItems.get(position).getOptionC());
                        errorQuestionInfo.setOptionD(dataItems.get(position).getOptionD());

                        long colunm = dbManager.insertErrorQuestion(errorQuestionInfo);

                        if (colunm == -1) {
                            Toast.makeText(mContext, "添加错误", Toast.LENGTH_SHORT).show();
                        }
                        holder.ivC.setImageResource(R.drawable.ic_practice_test_wrong);
                        holder.tvC.setTextColor(Color.parseColor("#d53235"));
                        //提示
                        holder.wrongLayout.setVisibility(View.VISIBLE);
                        //显示正确选项
                        if (dataItems.get(position).getCorrectAnswer().contains("A")) {
                            holder.ivA.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvA.setTextColor(Color.parseColor("#61bc31"));
                        } else if (dataItems.get(position).getCorrectAnswer().contains("B")) {
                            holder.ivB.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvB.setTextColor(Color.parseColor("#61bc31"));
                        } else if (dataItems.get(position).getCorrectAnswer().contains("C")) {
                            holder.ivC.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvC.setTextColor(Color.parseColor("#61bc31"));
                        } else if (dataItems.get(position).getCorrectAnswer().contains("D")) {
                            holder.ivD.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvD.setTextColor(Color.parseColor("#61bc31"));
                        }
                    }
                    //保存数据
                    SaveQuestionInfo questionInfo = new SaveQuestionInfo();
                    questionInfo.setQuestionId(dataItems.get(position).getTopicId());

                    questionInfo.setScore(2);
                    questionInfo.setIs_correct(isCorrect);
                    mContext.questionInfos.add(questionInfo);
                    dataItems.get(position).setYourAnswer("0");
                }
            });
            holder.layoutD.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (map.containsKey(position)) {
                        return;
                    }
                    map.put(position, true);
                    if (dataItems.get(position).getCorrectAnswer().contains("D")) {
                        mContext.setCurrentView(position + 1);
                        holder.ivD.setImageResource(R.drawable.ic_practice_test_right);
                        holder.tvD.setTextColor(Color.parseColor("#61bc31"));
                        isCorrect = ConstantUtil.isCorrect;
                    } else {
                        isCorrect = ConstantUtil.isError;
                        errortopicNum += 1;
                        //自动添加错误题目
                        ErrorQuestionInfo errorQuestionInfo = new ErrorQuestionInfo();
                        errorQuestionInfo.setTopic(dataItems.get(position).getTopic());
                        errorQuestionInfo.setCorrectAnswer(dataItems.get(position).getCorrectAnswer());

                        errorQuestionInfo.setYourAnswer("D");

                        errorQuestionInfo.setOptionA(dataItems.get(position).getOptionA());
                        errorQuestionInfo.setOptionB(dataItems.get(position).getOptionB());
                        errorQuestionInfo.setOptionC(dataItems.get(position).getOptionC());
                        errorQuestionInfo.setOptionD(dataItems.get(position).getOptionD());

                        long colunm = dbManager.insertErrorQuestion(errorQuestionInfo);

                        if (colunm == -1) {
                            Toast.makeText(mContext, "添加错误", Toast.LENGTH_SHORT).show();
                        }

                        holder.ivD.setImageResource(R.drawable.ic_practice_test_wrong);
                        holder.tvD.setTextColor(Color.parseColor("#d53235"));
                        //提示
                        holder.wrongLayout.setVisibility(View.VISIBLE);

                        //显示正确选项
                        if (dataItems.get(position).getCorrectAnswer().contains("A")) {
                            holder.ivA.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvA.setTextColor(Color.parseColor("#61bc31"));
                        } else if (dataItems.get(position).getCorrectAnswer().contains("B")) {
                            holder.ivB.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvB.setTextColor(Color.parseColor("#61bc31"));
                        } else if (dataItems.get(position).getCorrectAnswer().contains("C")) {
                            holder.ivC.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvC.setTextColor(Color.parseColor("#61bc31"));
                        } else if (dataItems.get(position).getCorrectAnswer().contains("D")) {
                            holder.ivD.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvD.setTextColor(Color.parseColor("#61bc31"));
                        }
                    }
                    //保存数据
                    SaveQuestionInfo questionInfo = new SaveQuestionInfo();
                    questionInfo.setQuestionId(dataItems.get(position).getTopicId());

                    questionInfo.setScore(2);
                    questionInfo.setIs_correct(isCorrect);
                    mContext.questionInfos.add(questionInfo);
                    dataItems.get(position).setYourAnswer("0");
                }
            });

        } else if (dataItems.get(position).getCorrectAnswer().length() > 1) {
            //多选题
            holder.question.setText("(多选题)" + dataItems.get(position).getTopic());

            holder.layoutA.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    mapClick.put(position, true);
                    if (map.containsKey(position)) {
                        return;
                    }
                    if (dataItems.get(position).getCorrectAnswer().contains("A")) {
                        holder.ivA.setImageResource(R.drawable.ic_practice_test_right);
                        holder.tvA.setTextColor(Color.parseColor("#61bc31"));
                        isCorrect = ConstantUtil.isCorrect;
                        if (position == viewItems.size() - 1) {
                            answerLast.append("A");
                        } else {
                            answer.append("A");
                        }
                    } else {
                        isCorrect = ConstantUtil.isError;
                        mapMultiSelect.put(position, isCorrect);
                        errortopicNum += 1;
                        //自动添加错误题目
                        ErrorQuestionInfo errorQuestionInfo = new ErrorQuestionInfo();
                        errorQuestionInfo.setTopic(dataItems.get(position).getTopic());

                        errorQuestionInfo.setCorrectAnswer(dataItems.get(position).getCorrectAnswer());

                        errorQuestionInfo.setYourAnswer("A");

                        errorQuestionInfo.setOptionA(dataItems.get(position).getOptionA());
                        errorQuestionInfo.setOptionB(dataItems.get(position).getOptionB());
                        errorQuestionInfo.setOptionC(dataItems.get(position).getOptionC());
                        errorQuestionInfo.setOptionD(dataItems.get(position).getOptionD());

                        long colunm = dbManager.insertErrorQuestion(errorQuestionInfo);

                        if (colunm == -1) {
                            Toast.makeText(mContext, "添加错误", Toast.LENGTH_SHORT).show();
                        }

                        map.put(position, true);
                        holder.ivA.setImageResource(R.drawable.ic_practice_test_wrong);
                        holder.tvA.setTextColor(Color.parseColor("#d53235"));
                        //提示
                        holder.wrongLayout.setVisibility(View.VISIBLE);

                        //显示正确选项
                        if (dataItems.get(position).getCorrectAnswer().contains("A")) {
                            holder.ivA.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvA.setTextColor(Color.parseColor("#61bc31"));
                        }
                        if (dataItems.get(position).getCorrectAnswer().contains("B")) {
                            holder.ivB.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvB.setTextColor(Color.parseColor("#61bc31"));
                        }
                        if (dataItems.get(position).getCorrectAnswer().contains("C")) {
                            holder.ivC.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvC.setTextColor(Color.parseColor("#61bc31"));
                        }
                        if (dataItems.get(position).getCorrectAnswer().contains("D")) {
                            holder.ivD.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvD.setTextColor(Color.parseColor("#61bc31"));
                        }

                        //保存数据
                        SaveQuestionInfo questionInfo = new SaveQuestionInfo();
                        questionInfo.setQuestionId(dataItems.get(position).getTopicId());

                        questionInfo.setScore(2);
                        questionInfo.setIs_correct(isCorrect);
                        mContext.questionInfos.add(questionInfo);
                        dataItems.get(position).setYourAnswer("0");
                    }
                    resultA = "A";
                }
            });
            holder.layoutB.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    mapClick.put(position, true);
                    if (map.containsKey(position)) {
                        return;
                    }
                    if (dataItems.get(position).getCorrectAnswer().contains("B")) {
                        holder.ivB.setImageResource(R.drawable.ic_practice_test_right);
                        holder.tvB.setTextColor(Color.parseColor("#61bc31"));
                        isCorrect = ConstantUtil.isCorrect;
                        if (position == viewItems.size() - 1) {
                            answerLast.append("B");
                        } else {
                            answer.append("B");
                        }
                    } else {
                        isCorrect = ConstantUtil.isError;
                        mapMultiSelect.put(position, isCorrect);
                        errortopicNum += 1;
                        //自动添加错误题目
                        ErrorQuestionInfo errorQuestionInfo = new ErrorQuestionInfo();
                        errorQuestionInfo.setTopic(dataItems.get(position).getTopic());
                        ;
                        errorQuestionInfo.setCorrectAnswer(dataItems.get(position).getCorrectAnswer());

                        errorQuestionInfo.setYourAnswer("B");

                        errorQuestionInfo.setOptionA(dataItems.get(position).getOptionA());
                        errorQuestionInfo.setOptionB(dataItems.get(position).getOptionB());
                        errorQuestionInfo.setOptionC(dataItems.get(position).getOptionC());
                        errorQuestionInfo.setOptionD(dataItems.get(position).getOptionD());

                        long colunm = dbManager.insertErrorQuestion(errorQuestionInfo);

                        if (colunm == -1) {
                            Toast.makeText(mContext, "添加错误", Toast.LENGTH_SHORT).show();
                        }

                        map.put(position, true);
                        holder.ivB.setImageResource(R.drawable.ic_practice_test_wrong);
                        holder.tvB.setTextColor(Color.parseColor("#d53235"));
                        //提示
                        holder.wrongLayout.setVisibility(View.VISIBLE);

                        //显示正确选项
                        if (dataItems.get(position).getCorrectAnswer().contains("A")) {
                            holder.ivA.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvA.setTextColor(Color.parseColor("#61bc31"));
                        }
                        if (dataItems.get(position).getCorrectAnswer().contains("B")) {
                            holder.ivB.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvB.setTextColor(Color.parseColor("#61bc31"));
                        }
                        if (dataItems.get(position).getCorrectAnswer().contains("C")) {
                            holder.ivC.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvC.setTextColor(Color.parseColor("#61bc31"));
                        }
                        if (dataItems.get(position).getCorrectAnswer().contains("D")) {
                            holder.ivD.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvD.setTextColor(Color.parseColor("#61bc31"));
                        }

                        //保存数据
                        SaveQuestionInfo questionInfo = new SaveQuestionInfo();
                        questionInfo.setQuestionId(dataItems.get(position).getTopicId());

                        questionInfo.setScore(2);
                        questionInfo.setIs_correct(isCorrect);
                        mContext.questionInfos.add(questionInfo);
                        dataItems.get(position).setYourAnswer("0");
                    }
                    resultB = "B";
                }
            });
            holder.layoutC.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    mapClick.put(position, true);
                    if (map.containsKey(position)) {
                        return;
                    }
                    if (dataItems.get(position).getCorrectAnswer().contains("C")) {
                        holder.ivC.setImageResource(R.drawable.ic_practice_test_right);
                        holder.tvC.setTextColor(Color.parseColor("#61bc31"));
                        isCorrect = ConstantUtil.isCorrect;
                        if (position == viewItems.size() - 1) {
                            answerLast.append("C");
                        } else {
                            answer.append("C");
                        }
                    } else {
                        isCorrect = ConstantUtil.isError;
                        mapMultiSelect.put(position, isCorrect);
                        errortopicNum += 1;
                        //自动添加错误题目
                        ErrorQuestionInfo errorQuestionInfo = new ErrorQuestionInfo();
                        errorQuestionInfo.setTopic(dataItems.get(position).getTopic());
                        ;
                        errorQuestionInfo.setCorrectAnswer(dataItems.get(position).getCorrectAnswer());

                        errorQuestionInfo.setYourAnswer("C");

                        errorQuestionInfo.setOptionA(dataItems.get(position).getOptionA());
                        errorQuestionInfo.setOptionB(dataItems.get(position).getOptionB());
                        errorQuestionInfo.setOptionC(dataItems.get(position).getOptionC());
                        errorQuestionInfo.setOptionD(dataItems.get(position).getOptionD());

                        long colunm = dbManager.insertErrorQuestion(errorQuestionInfo);

                        if (colunm == -1) {
                            Toast.makeText(mContext, "添加错误", Toast.LENGTH_SHORT).show();
                        }

                        map.put(position, true);
                        holder.ivC.setImageResource(R.drawable.ic_practice_test_wrong);
                        holder.tvC.setTextColor(Color.parseColor("#d53235"));
                        //提示
                        holder.wrongLayout.setVisibility(View.VISIBLE);

                        //显示正确选项
                        if (dataItems.get(position).getCorrectAnswer().contains("A")) {
                            holder.ivA.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvA.setTextColor(Color.parseColor("#61bc31"));
                        }
                        if (dataItems.get(position).getCorrectAnswer().contains("B")) {
                            holder.ivB.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvB.setTextColor(Color.parseColor("#61bc31"));
                        }
                        if (dataItems.get(position).getCorrectAnswer().contains("C")) {
                            holder.ivC.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvC.setTextColor(Color.parseColor("#61bc31"));
                        }
                        if (dataItems.get(position).getCorrectAnswer().contains("D")) {
                            holder.ivD.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvD.setTextColor(Color.parseColor("#61bc31"));
                        }

                        //保存数据
                        SaveQuestionInfo questionInfo = new SaveQuestionInfo();
                        questionInfo.setQuestionId(dataItems.get(position).getTopicId());

                        questionInfo.setScore(2);
                        questionInfo.setIs_correct(isCorrect);
                        mContext.questionInfos.add(questionInfo);
                        dataItems.get(position).setYourAnswer("0");
                    }
                    resultC = "C";
                }
            });
            holder.layoutD.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    mapClick.put(position, true);
                    if (map.containsKey(position)) {
                        return;
                    }
                    if (dataItems.get(position).getCorrectAnswer().contains("D")) {
                        holder.ivD.setImageResource(R.drawable.ic_practice_test_right);
                        holder.tvD.setTextColor(Color.parseColor("#61bc31"));
                        isCorrect = ConstantUtil.isCorrect;
                        if (position == viewItems.size() - 1) {
                            answerLast.append("D");
                        } else {
                            answer.append("D");
                        }
                    } else {
                        isCorrect = ConstantUtil.isError;
                        mapMultiSelect.put(position, isCorrect);
                        errortopicNum += 1;
                        //自动添加错误题目
                        ErrorQuestionInfo errorQuestionInfo = new ErrorQuestionInfo();
                        errorQuestionInfo.setTopic(dataItems.get(position).getTopic());

                        errorQuestionInfo.setCorrectAnswer(dataItems.get(position).getCorrectAnswer());

                        errorQuestionInfo.setYourAnswer("D");

                        errorQuestionInfo.setOptionA(dataItems.get(position).getOptionA());
                        errorQuestionInfo.setOptionB(dataItems.get(position).getOptionB());
                        errorQuestionInfo.setOptionC(dataItems.get(position).getOptionC());
                        errorQuestionInfo.setOptionD(dataItems.get(position).getOptionD());

                        long colunm = dbManager.insertErrorQuestion(errorQuestionInfo);

                        if (colunm == -1) {
                            Toast.makeText(mContext, "添加错误", Toast.LENGTH_SHORT).show();
                        }

                        map.put(position, true);
                        holder.ivD.setImageResource(R.drawable.ic_practice_test_wrong);
                        holder.tvD.setTextColor(Color.parseColor("#d53235"));
                        //提示
                        holder.wrongLayout.setVisibility(View.VISIBLE);

                        //显示正确选项
                        if (dataItems.get(position).getCorrectAnswer().contains("A")) {
                            holder.ivA.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvA.setTextColor(Color.parseColor("#61bc31"));
                        }
                        if (dataItems.get(position).getCorrectAnswer().contains("B")) {
                            holder.ivB.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvB.setTextColor(Color.parseColor("#61bc31"));
                        }
                        if (dataItems.get(position).getCorrectAnswer().contains("C")) {
                            holder.ivC.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvC.setTextColor(Color.parseColor("#61bc31"));
                        }
                        if (dataItems.get(position).getCorrectAnswer().contains("D")) {
                            holder.ivD.setImageResource(R.drawable.ic_practice_test_right);
                            holder.tvD.setTextColor(Color.parseColor("#61bc31"));
                        }

                        //保存数据
                        SaveQuestionInfo questionInfo = new SaveQuestionInfo();
                        questionInfo.setQuestionId(dataItems.get(position).getTopicId());

                        questionInfo.setScore(2);
                        questionInfo.setIs_correct(isCorrect);
                        mContext.questionInfos.add(questionInfo);
                        dataItems.get(position).setYourAnswer("0");
                    }
                    resultD = "D";
                }
            });

        }
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.parseColor("#2b89e9"));
        SpannableStringBuilder builder1 = new SpannableStringBuilder(holder.question.getText().toString());
        builder1.setSpan(blueSpan, 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.question.setText(builder1);

        // 最后一页修改"下一步"按钮文字
        if (position == viewItems.size() - 1) {
            holder.nextText.setText("提交");
            holder.nextImage.setImageResource(R.drawable.vote_submit_finish);
        }
        holder.previousBtn.setOnClickListener(new LinearOnClickListener(position - 1, false, position, holder));
        holder.nextBtn.setOnClickListener(new LinearOnClickListener(position + 1, true, position, holder));
        container.addView(viewItems.get(position));
        return viewItems.get(position);
    }

    /**
     * @author 设置上一步和下一步按钮监听
     */
    class LinearOnClickListener implements OnClickListener {

        private int mPosition;
        private int mPosition1;
        private boolean mIsNext;
        private ViewHolder viewHolder;

        public LinearOnClickListener(int position, boolean mIsNext, int position1, ViewHolder viewHolder) {
            mPosition = position;
            mPosition1 = position1;
            this.viewHolder = viewHolder;
            this.mIsNext = mIsNext;
        }

        @Override
        public void onClick(View v) {
            if (mPosition == viewItems.size()) {
                //单选
                if (dataItems.get(mPosition1).getCorrectAnswer().length() == 1) {
                    if (!map.containsKey(mPosition1)) {
                        Toast.makeText(mContext, "请选择选项", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mContext.uploadExamination(errortopicNum);
                } else if (dataItems.get(mPosition1).getCorrectAnswer().length() > 1) {
                    //判断多选时的点击
                    if (!map.containsKey(mPosition1)) {
                        if (!mapClick.containsKey(mPosition1)) {
                            Toast.makeText(mContext, "请选择选项", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    map.put(mPosition1, true);

                    if (mapMultiSelect.containsKey(mPosition1)) {
                        //提交答题
                        mContext.uploadExamination(errortopicNum);
                    } else {
                        String ssStr = dataItems.get(mPosition1).getCorrectAnswer();
                        ssStr = ssStr.replace("|", "");

                        if (mPosition == viewItems.size()) {
                            if (answerLast.toString().contains("1")) {
                                answer1.append("A");
                            }
                            if (answerLast.toString().contains("2")) {
                                answer1.append("B");
                            }
                            if (answerLast.toString().contains("3")) {
                                answer1.append("C");
                            }
                            if (answerLast.toString().contains("4")) {
                                answer1.append("D");
                            }

                        } else {
                            if (answer.toString().contains("1")) {
                                answer1.append("A");
                            }
                            if (answer.toString().contains("2")) {
                                answer1.append("B");
                            }
                            if (answer.toString().contains("3")) {
                                answer1.append("C");
                            }
                            if (answer.toString().contains("4")) {
                                answer1.append("D");
                            }
                        }

                        if (answer1.toString().equals(ssStr)) {
                            //清除答案
                            answer.delete(0, answer.length());
                            answer1.delete(0, answer1.length());
                            isCorrect = ConstantUtil.isCorrect;
                            mapMultiSelect.put(mPosition1, ConstantUtil.isCorrect);
                            //保存数据
                            SaveQuestionInfo questionInfo = new SaveQuestionInfo();
                            questionInfo.setQuestionId(dataItems.get(mPosition1).getTopicId());
                            questionInfo.setScore(2);
                            questionInfo.setIs_correct(isCorrect);
                            mContext.questionInfos.add(questionInfo);
                            dataItems.get(mPosition1).setYourAnswer("0");
                            //提交答题
                            mContext.uploadExamination(errortopicNum);
                        } else {
                            //清除答案
                            answer.delete(0, answer.length());
                            answer1.delete(0, answer1.length());
                            errortopicNum += 1;
                            isCorrect = ConstantUtil.isError;
                            //自动添加错误题目
                            ErrorQuestionInfo errorQuestionInfo = new ErrorQuestionInfo();
                            errorQuestionInfo.setTopic(dataItems.get(mPosition1).getTopic());
                            errorQuestionInfo.setCorrectAnswer(dataItems.get(mPosition1).getCorrectAnswer());
                            errorQuestionInfo.setYourAnswer(answer.toString());

                            long colunm = dbManager.insertErrorQuestion(errorQuestionInfo);

                            if (colunm == -1) {
                                Toast.makeText(mContext, "添加错误", Toast.LENGTH_SHORT).show();
                            }
                            isCorrect = ConstantUtil.isError;
                            mapMultiSelect.put(mPosition1, ConstantUtil.isError);

                            //提示
                            viewHolder.wrongLayout.setVisibility(View.VISIBLE);
                            //保存数据
                            SaveQuestionInfo questionInfo = new SaveQuestionInfo();
                            questionInfo.setQuestionId(dataItems.get(mPosition1).getTopicId());
                            questionInfo.setScore(2);
                            questionInfo.setIs_correct(isCorrect);
                            mContext.questionInfos.add(questionInfo);
                            dataItems.get(mPosition1).setYourAnswer("0");
                            //显示正确选项
                            if (dataItems.get(mPosition1).getCorrectAnswer().contains("1")) {
                                viewHolder.ivA.setImageResource(R.drawable.ic_practice_test_right);
                                viewHolder.tvA.setTextColor(Color.parseColor("#61bc31"));
                            }
                            if (dataItems.get(mPosition1).getCorrectAnswer().contains("2")) {
                                viewHolder.ivB.setImageResource(R.drawable.ic_practice_test_right);
                                viewHolder.tvB.setTextColor(Color.parseColor("#61bc31"));
                            }
                            if (dataItems.get(mPosition1).getCorrectAnswer().contains("3")) {
                                viewHolder.ivC.setImageResource(R.drawable.ic_practice_test_right);
                                viewHolder.tvC.setTextColor(Color.parseColor("#61bc31"));
                            }
                            if (dataItems.get(mPosition1).getCorrectAnswer().contains("4")) {
                                viewHolder.ivD.setImageResource(R.drawable.ic_practice_test_right);
                                viewHolder.tvD.setTextColor(Color.parseColor("#61bc31"));
                            }
                        }

                    }
                }
            } else {
                if (mPosition == -1) {
                    Toast.makeText(mContext, "已经是第一页", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //单选
                    if (dataItems.get(mPosition1).getCorrectAnswer().length() == 1) {
                        if (mIsNext) {
                            if (!map.containsKey(mPosition1)) {
                                Toast.makeText(mContext, "请选择选项", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        isNext = mIsNext;
                        mContext.setCurrentView(mPosition);
                    } else if (dataItems.get(mPosition1).getCorrectAnswer().length() > 1) {
                        if (mIsNext) {
                            //判断多选时的点击
                            if (!map.containsKey(mPosition1)) {
                                if (!mapClick.containsKey(mPosition1)) {
                                    Toast.makeText(mContext, "请选择选项", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            map.put(mPosition1, true);

                            if (mapMultiSelect.containsKey(mPosition1)) {
                                //清除答案
                                answer.delete(0, answer.length());
                                //选过的，直接跳转下一题
                                isNext = mIsNext;
                                mContext.setCurrentView(mPosition);
                            } else {
                                String ssStr = dataItems.get(mPosition1).getCorrectAnswer();
                                ssStr = ssStr.replace("|", "");
                                if (answer.toString().contains("1")) {
                                    answer1.append("A");
                                }
                                if (answer.toString().contains("2")) {
                                    answer1.append("B");
                                }
                                if (answer.toString().contains("3")) {
                                    answer1.append("C");
                                }
                                if (answer.toString().contains("4")) {
                                    answer1.append("D");
                                }
                                if (answer1.toString().equals(ssStr)) {
                                    //清除答案
                                    answer.delete(0, answer.length());
                                    answer1.delete(0, answer1.length());
                                    isCorrect = ConstantUtil.isCorrect;
                                    mapMultiSelect.put(mPosition1, ConstantUtil.isCorrect);
                                    //保存数据
                                    SaveQuestionInfo questionInfo = new SaveQuestionInfo();
                                    questionInfo.setQuestionId(dataItems.get(mPosition1).getTopicId());
                                    questionInfo.setScore(2);
                                    questionInfo.setIs_correct(isCorrect);
                                    mContext.questionInfos.add(questionInfo);
                                    dataItems.get(mPosition1).setYourAnswer("0");
                                    isNext = mIsNext;
                                    mContext.setCurrentView(mPosition);
                                } else {
                                    //清除答案
                                    answer.delete(0, answer.length());
                                    answer1.delete(0, answer1.length());
                                    errortopicNum += 1;
                                    isCorrect = ConstantUtil.isError;
                                    //自动添加错误题目
                                    ErrorQuestionInfo errorQuestionInfo = new ErrorQuestionInfo();
                                    errorQuestionInfo.setTopic(dataItems.get(mPosition1).getTopic());
                                    errorQuestionInfo.setCorrectAnswer(dataItems.get(mPosition1).getCorrectAnswer());

                                    errorQuestionInfo.setYourAnswer(answer.toString());
                                    errorQuestionInfo.setOptionA(dataItems.get(mPosition1).getOptionA());
                                    errorQuestionInfo.setOptionB(dataItems.get(mPosition1).getOptionB());
                                    errorQuestionInfo.setOptionC(dataItems.get(mPosition1).getOptionC());
                                    errorQuestionInfo.setOptionD(dataItems.get(mPosition1).getOptionD());

                                    long colunm = dbManager.insertErrorQuestion(errorQuestionInfo);

                                    if (colunm == -1) {
                                        Toast.makeText(mContext, "添加错误", Toast.LENGTH_SHORT).show();
                                    }
                                    isCorrect = ConstantUtil.isError;
                                    mapMultiSelect.put(mPosition1, ConstantUtil.isError);

                                    //提示
                                    viewHolder.wrongLayout.setVisibility(View.VISIBLE);
                                    //保存数据
                                    SaveQuestionInfo questionInfo = new SaveQuestionInfo();
                                    questionInfo.setQuestionId(dataItems.get(mPosition1).getTopicId());
                                    questionInfo.setScore(2);
                                    questionInfo.setIs_correct(isCorrect);
                                    mContext.questionInfos.add(questionInfo);
                                    dataItems.get(mPosition1).setYourAnswer("0");
                                    //显示正确选项
                                    if (dataItems.get(mPosition1).getCorrectAnswer().contains("A")) {
                                        viewHolder.ivA.setImageResource(R.drawable.ic_practice_test_right);
                                        viewHolder.tvA.setTextColor(Color.parseColor("#61bc31"));
                                    }
                                    if (dataItems.get(mPosition1).getCorrectAnswer().contains("B")) {
                                        viewHolder.ivB.setImageResource(R.drawable.ic_practice_test_right);
                                        viewHolder.tvB.setTextColor(Color.parseColor("#61bc31"));
                                    }
                                    if (dataItems.get(mPosition1).getCorrectAnswer().contains("C")) {
                                        viewHolder.ivC.setImageResource(R.drawable.ic_practice_test_right);
                                        viewHolder.tvC.setTextColor(Color.parseColor("#61bc31"));
                                    }
                                    if (dataItems.get(mPosition1).getCorrectAnswer().contains("D")) {
                                        viewHolder.ivD.setImageResource(R.drawable.ic_practice_test_right);
                                        viewHolder.tvD.setTextColor(Color.parseColor("#61bc31"));
                                    }
                                }
                            }
                        } else {
                            mContext.setCurrentView(mPosition);
                        }

                    } else {
                        if (mIsNext) {
                            if (!map.containsKey(mPosition1)) {
                                Toast.makeText(mContext, "请选择选项", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        isNext = mIsNext;
                        mContext.setCurrentView(mPosition);
                    }
                }
            }

        }

    }

    @Override
    public int getCount() {
        if (viewItems == null)
            return 0;
        return viewItems.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    //错题数
    public int errorTopicNum() {
        if (errortopicNum != 0) {
            return errortopicNum;
        }
        return 0;
    }

    public class ViewHolder {
        TextView questionType;
        TextView question;
        LinearLayout previousBtn, nextBtn, errorBtn;
        TextView nextText;
        TextView totalText;
        ImageView nextImage;
        LinearLayout wrongLayout;
        TextView explaindetailTv;
        LinearLayout layoutA;
        LinearLayout layoutB;
        LinearLayout layoutC;
        LinearLayout layoutD;
        ImageView ivA;
        ImageView ivB;
        ImageView ivC;
        ImageView ivD;
        TextView tvA;
        TextView tvB;
        TextView tvC;
        TextView tvD;
    }

}
