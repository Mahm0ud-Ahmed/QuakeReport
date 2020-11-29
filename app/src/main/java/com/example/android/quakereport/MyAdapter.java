package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyAdapter extends BaseAdapter {

    private Context context;
    private List<EarthquakeData> data;

    public MyAdapter(Context context, List<EarthquakeData> data) {
        this.context = context;
        this.data = data;
    }

    //داله تقوم على تنسيق التاريخ
    private String formatData(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
        return dateFormat.format(date);
    }

    //دالة تقوم على تنسيق الوقت
    private String formatTime(Date time) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        return timeFormat.format(time);
    }

    // داله تقوم على تقسيم النص الخاص بالمكان الى نصين لتوزيعهم على عنصري عرض
    private String splitPlace(String place, int i) {
        String[] arr = new String[2];
        int index = place.indexOf("of");
        if (place.contains("of")) {
            index = place.indexOf("of") + 2;
            arr[0] = place.substring(0, index);
        }else {
            arr[0] = null;
        }
        arr[1] = place.substring(index + 1, place.length());
        return arr[i];
    }

    //داله تقوم على اختيار اللون المناسب على حسب قوة الزلزال وحفظ قيمته
    private int getMagColor(double mag) {
        int magnitudeColorResourceId;
        int magFloor = (int) Math.floor(mag);
        switch (magFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        //يتم هنا تحويل مسار اللون المختار الى قيمه حقيقيه يمكن الاستفاده منها
        return ContextCompat.getColor(context, magnitudeColorResourceId);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public EarthquakeData getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setItem(List<EarthquakeData> item) {
        data.add((EarthquakeData) item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EarthquakeData earthquakeData = getItem(position);
        ViewHolder viewHolder;
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.templet_activity, parent, false);
            viewHolder = new ViewHolder();
            //عمل inflat لعناصر العرض على الشاشه
            viewHolder.tv_mag = v.findViewById(R.id.tv_mag);
            viewHolder.tv_location = v.findViewById(R.id.tv_location);
            viewHolder.tv_place = v.findViewById(R.id.tv_place);
            viewHolder.tv_date = v.findViewById(R.id.tv_date);
            viewHolder.tv_time = v.findViewById(R.id.tv_time);

            // حفظ عملية ال inflat لل Views داخل دالة setTag من View Class وهذه العملية تتم مره واحده فقط
            v.setTag(viewHolder);
        } else {
            // جلب عملية ال inflat التي تمت من دالة getTag وحفظها ب viewHolder Object
            viewHolder = (ViewHolder) v.getTag();
        }

        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        String mag = decimalFormat.format(earthquakeData.getMagnitude());
        // ارسال قيمة لاحد خصائص ال viewHolder من خلال كائن earthquakeData
        viewHolder.tv_mag.setText(mag);
        // استدعاء هذا الكلاس لتمرير قيمة اللون المرسله من دالة getMagColor الى العنصر المراد من خلاله
        GradientDrawable gradientDrawable = (GradientDrawable) viewHolder.tv_mag.getBackground();
        gradientDrawable.setColor(getMagColor(earthquakeData.getMagnitude()));

        // عمل متغير لحفظ قيمة ال location
        String temp = splitPlace(earthquakeData.getLocation(), 0);
        if (temp == null){
            // اذا كانت القيمة فارغه سيتم اخفاء
            viewHolder.tv_location.setVisibility(View.GONE);
        } else {
            viewHolder.tv_location.setText(temp);
        }
        // ارسال قيمة لاحد خصائص ال viewHolder من خلال كائن earthquakeData
        viewHolder.tv_place.setText(splitPlace(earthquakeData.getLocation(), 1));


        // يتم هنا تحويل قيمة التاريخ والوقت من تقويم Unix الى تقويم نستطيع قراءته وفهمه
        Date date = new Date(earthquakeData.getDate());
        String dateFormat = formatData(date);
        String timeFormat = formatTime(date);
        // ارسال قيمة لاحد خصائص ال viewHolder من خلال كائن earthquakeData
        viewHolder.tv_date.setText(dateFormat);
        // ارسال قيمة لاحد خصائص ال viewHolder من خلال كائن earthquakeData
        viewHolder.tv_time.setText(timeFormat);


        return v;
    }

    // كلاس لتقليل عملية ال inflat داخل دالة getView مما يؤدي لتسريع عملية العرض
    private static class ViewHolder{
        TextView tv_mag, tv_location, tv_place, tv_date, tv_time;
    }
}
