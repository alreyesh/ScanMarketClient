package alreyesh.android.scanmarketclient.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import alreyesh.android.scanmarketclient.fragments.PurchaseHistoryFragment;
import alreyesh.android.scanmarketclient.fragments.PurchasePendingFragment;

public class MyViewPagerAdapter extends FragmentStateAdapter {


    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
       switch(position){
           case 0:
               return new PurchasePendingFragment();
           case 1:
               return new PurchaseHistoryFragment();
           default:
               return new PurchasePendingFragment();

       }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
