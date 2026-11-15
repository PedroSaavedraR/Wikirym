package es.udc.ps.wikirym.app.data;

import es.udc.ps.wikirym.app.domain.CategoryMembersResponse;
import es.udc.ps.wikirym.app.domain.SearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UespAPI {

    // https://en.uesp.net/w/api.php?action=query&list=categorymembers&cmtitle=Category:Skyrim-NPCs&cmlimit=50&format=json&formatversion=2
    // format and formatversion are added by UespApiInterceptor
    @GET("api.php?action=query&list=categorymembers")
    Call<CategoryMembersResponse> getCategoryMembers(@Query("cmtitle") String category,
                                                     @Query("cmlimit") int limit);

    // https://en.uesp.net/w/api.php?action=query&list=search&srnamespace=134&srprop=&srsearch=lydia%20incategory:Skyrim-NPCs&srlimit=50&format=json&formatversion=2
    // srnamespace=134 is the Skyrim namespace: without it, incategory: finds nothing
    @GET("api.php?action=query&list=search&srnamespace=134&srprop=")
    Call<SearchResponse> searchPages(@Query("srsearch") String search,
                                     @Query("srlimit") int limit);
}
