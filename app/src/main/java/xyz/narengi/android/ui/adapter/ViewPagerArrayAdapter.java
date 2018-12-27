package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import xyz.narengi.android.R;

/**
 * @author Siavash Mahmoudpour
 */
//public class ViewPagerArrayAdapter extends ArrayAdapter<Object> {
public class ViewPagerArrayAdapter extends RecyclerView.Adapter<ViewPagerArrayAdapter.ViewHolder> {

    private Context context;
    private int resource;
    private List<Object> objects;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.explore_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemView);
        viewHolder.viewPager = (ViewPager)itemView.findViewById(R.id.viewpager);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        Object object = objects.get(position);

        String[] rank = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };

        String[] country = new String[] { "China", "India", "United States",
                "Indonesia", "Brazil", "Pakistan", "Nigeria", "Bangladesh",
                "Russia", "Japan" };

        String[] population = new String[] { "1,354,040,000", "1,210,193,422",
                "315,761,000", "237,641,326", "193,946,886", "182,912,000",
                "170,901,000", "152,518,015", "143,369,806", "127,360,000" };

        int[] flag = new int[] {};

        // Locate the ViewPager in viewpager_main.xml
//            ViewPager viewPager = (ViewPager) rowView.findViewById(R.id.viewpager);
//            viewPager.setId(1000+position);
        viewHolder.viewPager.setId(1000+position);
        // Pass results to ViewPagerAdapter Class
        ViewPagerAdapter adapter = new ViewPagerAdapter(context, rank, country, population, flag);
        // Binds the Adapter to the ViewPager
        viewHolder.viewPager.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewPager viewPager;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public ViewPagerArrayAdapter(List<Object> objects) {
        this.objects = objects;
    }

    public class ViewPagerAdapter extends PagerAdapter {
        // Declare Variables
        Context context;
        String[] rank;
        String[] country;
        String[] population;
        int[] flag;
        LayoutInflater inflater;

        public ViewPagerAdapter(Context context, String[] rank, String[] country,
                                String[] population, int[] flag) {
            this.context = context;
            this.rank = rank;
            this.country = country;
            this.population = population;
            this.flag = flag;
        }

        @Override
        public int getCount() {
            return rank.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            // Declare Variables
            TextView txtrank;
            TextView txtcountry;
            TextView txtpopulation;
            ImageView imgflag;

            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.explore_viewpager_item, container,
                    false);

            // Locate the TextViews in viewpager_item.xml
//            txtrank = (TextView) itemView.findViewById(R.id.rank);
//            txtcountry = (TextView) itemView.findViewById(R.id.country);
//            txtpopulation = (TextView) itemView.findViewById(R.id.population);
//
//            // Capture position and set to the TextViews
//            txtrank.setText(rank[position]);
//            txtcountry.setText(country[position]);
//            txtpopulation.setText(population[position]);

            // Locate the ImageView in viewpager_item.xml
            imgflag = (ImageView) itemView.findViewById(R.id.explore_viewpager_item_image);
            // Capture position and set to the ImageView
            imgflag.setImageResource(flag[position]);

            // Add viewpager_item.xml to ViewPager
            ((ViewPager) container).addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // Remove viewpager_item.xml from ViewPager
            ((ViewPager) container).removeView((RelativeLayout) object);

        }
    }
}
