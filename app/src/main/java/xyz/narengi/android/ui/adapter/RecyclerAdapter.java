package xyz.narengi.android.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import xyz.narengi.android.R;

/**
 * @author Siavash Mahmoudpour
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<Object> objects;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ViewPager viewPager;
        public ViewHolder(View view) {
            super(view);
//            this.viewPager = viewPager;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerAdapter(List<Object> objects, Context context) {
        this.objects = objects;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.explore_list_item, parent, false);

        ViewPager viewPager = (ViewPager)itemView.findViewById(R.id.viewpager);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
//
            viewPager.getLayoutParams().height = (int)(dpHeight/3);

        ViewHolder holder = new ViewHolder(itemView);
        holder.viewPager = viewPager;
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.mTextView.setText(mDataset[position]);

        Object object = objects.get(position);

        String[] rank = new String[] { "1", "2", "3", "4", "5", "6", "7"};

        String[] country = new String[] { "China", "India", "United States",
                "Indonesia", "Brazil", "Pakistan", "Nigeria"};

        String[] population = new String[] { "1,354,040,000", "1,210,193,422",
                "315,761,000", "237,641,326", "193,946,886", "182,912,000",
                "170,901,000"};

        int[] flag = new int[] { R.drawable.pic_0, R.drawable.pic_1,
                R.drawable.pic_2, R.drawable.pic_3,
                R.drawable.pic_4, R.drawable.pic_5, R.drawable.pic_6};

        holder.viewPager.setId(1000+position);
        ViewPagerAdapter adapter = new ViewPagerAdapter(context, rank, country, population, flag);
        holder.viewPager.setAdapter(adapter);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return objects.size();
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
            imgflag = (ImageView) itemView.findViewById(R.id.flag);
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
