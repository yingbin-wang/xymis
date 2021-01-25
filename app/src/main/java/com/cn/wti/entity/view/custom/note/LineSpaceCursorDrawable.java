package com.cn.wti.entity.view.custom.note;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ShapeDrawable;
import android.text.Editable;
import android.text.style.ImageSpan;
import android.widget.EditText;

import com.wticn.wyb.wtiapp.R;

/**
 * Created by wyb on 2017/4/21.
 */

public class LineSpaceCursorDrawable  extends ShapeDrawable {

    private Context context;
    private EditText view;

    public LineSpaceCursorDrawable(Context context, EditText view) {
        this.context = context;
        setDither(false);
        Resources res = view.getResources();
        getPaint().setColor(res.getColor(R.color.line));//R.color.note_edittext_cursor_color));
        setIntrinsicWidth((int)DisplayUtils.dip2px(context, 2));//res.getDimensionPixelSize(R.dimen.detail_notes_text_cursor_width));
        this.view = view;
    }

    public void setBounds(int left, int top, int right, int bottom) {

        Editable s = view.getText();
        ImageSpan[] imageSpans = s.getSpans(0, s.length(),
                ImageSpan.class);
        int selectionStart = view.getSelectionStart();
        for (ImageSpan span : imageSpans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            if (selectionStart >= start && selectionStart <= end)
            {
                super.setBounds(left, top, right, top - 1);
                return;
            }
        }
        super.setBounds(left, top, right, top + view.getLineHeight() - (int) ViewUtils.getLineSpacingExtra(context, view));

    }
}
