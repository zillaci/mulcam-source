package com.destroy.ddopagoihanda;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainListAdapter extends ArrayAdapter<StudentVO> {
    Context context;
    ArrayList<StudentVO> data;
    int resId;

    public MainListAdapter(Context context, int resource, ArrayList<StudentVO> data) {
        super(context, resource);

        this.context = context;
        this.resId = resource;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resId, null);

            MainListWrapper wrapper = new MainListWrapper(convertView);
            convertView.setTag(wrapper);
        }

        MainListWrapper wrapper = (MainListWrapper) convertView.getTag();

        ImageView studentImageView = wrapper.getStudentImageView();
        TextView nameView = wrapper.getNameView();
        TextView scoreView = wrapper.getScoreView();
        final ImageView contactView = wrapper.getContactView();

        final StudentVO studentVO = data.get(position);

        nameView.setText(studentVO.name);
        scoreView.setText(studentVO.score != 0 ? String.valueOf(studentVO.score) : "0");

        if (studentVO.photo != null && !studentVO.photo.equals("")) {
            Bitmap bitmap = BitmapFactory.decodeFile(studentVO.photo);
            if (bitmap != null) {
                studentImageView.setImageBitmap(bitmap);
            }
        }
        else {
            studentImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_student_small));
        }

        studentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View root = inflater.inflate(R.layout.dialog_student_image, null);

                ImageView dialogImageView = (ImageView) root.findViewById(R.id.dialog_image);
                if(studentVO.photo != null && !studentVO.photo.equals("")) {
                    Bitmap bitmap = BitmapFactory.decodeFile(studentVO.photo);
                    if(bitmap != null) {
                        dialogImageView.setImageBitmap(bitmap);
                    }
                }
                else {
                    dialogImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_student_large));
                }

                builder.setView(root);

                AlertDialog imageDialog = builder.create();
                imageDialog.setCanceledOnTouchOutside(true);
                imageDialog.show();
            }
        });

        if(studentVO.phone != null && !studentVO.phone.equals("")) {
            contactView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + studentVO.phone));
                    context.startActivity(intent);
                }
            });
        }
        else {
            contactView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(context, context.getString(R.string.main_list_phone_error), Toast.LENGTH_SHORT).show();
                }
            });
        }

        return convertView;
    }
}
