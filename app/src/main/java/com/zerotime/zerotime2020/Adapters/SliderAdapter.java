package com.zerotime.zerotime2020.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.zerotime.zerotime2020.R;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    public int[] slideImages = {
            R.drawable.proficciency,
            R.drawable.s_deliver_everywhere1,
            R.drawable.s_comfot

    };

    public String[] headings = {
            "مرحباً بك في زيرو تايم",
            "", ""
    };
    public String[] descriptions = {
            "يتم نقل البضائع بإحترافيه حتي لا تتسبب بأى ضرر \n هدفنا الأول والأساسي هو سلامه الشحنات ولهذا حصلنا علي ثقتكم الغاليه \n ونعدكم ان نكون دائماً عند حسن ظنكم",
            "مع زيرو تايم شحنتك ستصل من اى مكان إلى اى مكان داخل مصر \n فقط إعتمد علينا ولا تشغل بالك بالمسافات ",
            "لدينا فريق كامل متدرب على أعلى المستويات \n هدفه إراحتك فنحن نعمل ليلاً ونهاراً من أجلك انت فإرضاء العميل غايتنا الأولي والأخيره "
    };


    @Override
    public int getCount() {
        return descriptions.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView imageView = view.findViewById(R.id.imageSlider);
        TextView heading = view.findViewById(R.id.heading);
        TextView description = view.findViewById(R.id.description);

        imageView.setImageResource(slideImages[position]);
        heading.setText(headings[position]);
        description.setText(descriptions[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
