import com.google.inject.AbstractModule;
import data.DataModule;
import identity.IdentityModule;

public class ApplicationModule extends AbstractModule {
    protected void configure() {
        install(new IdentityModule());
        install(new DataModule());
    }
}
