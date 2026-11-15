package es.udc.ps.wikirym.app.data;

import java.util.List;

import es.udc.ps.wikirym.BuildConfig;
import es.udc.ps.wikirym.app.domain.WikiEntry;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface UespAPI {

    // https://en.uesp.net/w/api.php?action=query&list=categorymembers&cmtitle=Category:Skyrim-Quests&cmlimit=50&format=json&formatversion=2
    // UESP asks API clients to identify themselves with a descriptive User-Agent
    @Headers("User-Agent: Wikirym/" + BuildConfig.VERSION_NAME + " (https://github.com/PedroSaavedraR/Wikirym)")
    @GET("api.php?action=query&list=categorymembers")
    Call<List<WikiEntry>> getCategoryMembers(@Query("cmtitle") String category,
                                             @Query("cmlimit") int limit,
                                             @Query("format") String format,
                                             @Query("formatversion") int formatVersion);
}
