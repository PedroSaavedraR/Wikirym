package es.udc.ps.wikirym.app.data;

import es.udc.ps.wikirym.app.domain.CategoryMembersResponse;
import es.udc.ps.wikirym.app.domain.PageDetailResponse;
import es.udc.ps.wikirym.app.domain.SearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UespAPI {

    // https://en.uesp.net/w/api.php?action=query&list=categorymembers&cmtitle=Category:Skyrim-NPCs&cmlimit=50&format=json&formatversion=2
    // format y formatversion los añade UespApiInterceptor
    @GET("api.php?action=query&list=categorymembers")
    Call<CategoryMembersResponse> getCategoryMembers(@Query("cmtitle") String category,
                                                     @Query("cmlimit") int limit);

    // https://en.uesp.net/w/api.php?action=query&list=search&srnamespace=134&srprop=&srsearch=lydia%20incategory:Skyrim-NPCs&srlimit=50&format=json&formatversion=2
    // srnamespace=134 (Skyrim): sin él, incategory: no encuentra nada
    @GET("api.php?action=query&list=search&srnamespace=134&srprop=")
    Call<SearchResponse> searchPages(@Query("srsearch") String search,
                                     @Query("srlimit") int limit);

    // https://en.uesp.net/w/api.php?action=query&prop=extracts|pageimages&exintro=1&explaintext=1&piprop=thumbnail&pithumbsize=480&pageids=109982&format=json&formatversion=2
    // Introducción en texto plano e imagen principal de una página
    @GET("api.php?action=query&prop=extracts|pageimages&exintro=1&explaintext=1&piprop=thumbnail&pithumbsize=480")
    Call<PageDetailResponse> getPageDetail(@Query("pageids") int pageId);
}
