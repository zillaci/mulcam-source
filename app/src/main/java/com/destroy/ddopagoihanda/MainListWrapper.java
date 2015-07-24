package com.destroy.ddopagoihanda;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainListWrapper {
    View root;

    ImageView studentImageView;
    TextView nameView;
    TextView scoreView;
    ImageView contactView;

    public MainListWrapper(View root) {
        this.root = root;
    }

    public ImageView getStudentImageView() {
        if(studentImageView == null) {
            studentImageView = (ImageView) root.findViewById(R.id.main_item_student_image);
        }

        return studentImageView;
    }

    public TextView getNameView() {
        return nameView == null ? nameView = (TextView) root.findViewById(R.id.main_item_name) : nameView;
    }

    public TextView getScoreView() {
        return scoreView == null ? scoreView = (TextView) root.findViewById(R.id.main_item_score) : scoreView;
    }

    public ImageView getContactView() {
        return contactView == null ? contactView = (ImageView) root.findViewById(R.id.main_item_contact) : contactView;
    }
}
