package xyz.narengi.android.armin.dependency.components;

import javax.inject.Singleton;

import dagger.Component;
import xyz.narengi.android.armin.dependency.modules.AppModule;
import xyz.narengi.android.armin.dependency.modules.NetModule;
import xyz.narengi.android.armin.presenter.main.ExplorePresenter;

/**
 * Created by arminghm on 1/25/17.
 */

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {
    void inject(ExplorePresenter explorePresenter);
}
