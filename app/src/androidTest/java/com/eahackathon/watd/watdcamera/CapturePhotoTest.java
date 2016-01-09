package com.eahackathon.watd.watdcamera;

import android.support.test.rule.ActivityTestRule;
import android.util.Log;
import android.view.SurfaceHolder;

import com.eahackathon.watd.watdcamera.models.ResponseModel;
import com.eahackathon.watd.watdcamera.network.APIService;
import com.eahackathon.watd.watdcamera.network.WaTDAPI;
import com.squareup.okhttp.RequestBody;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by KienDu on 1/9/2016.
 */
public class CapturePhotoTest {

    private MainActivity mActivity;
    private WaTDAPI mMockWaTDAPI;
    private ArgumentCaptor<Callback> mCallCaptor;
    private Call<ResponseModel> mMockCall;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        mActivity = mActivityTestRule.getActivity();
        // Mock
        mMockWaTDAPI = Mockito.mock(WaTDAPI.class);
        mCallCaptor = ArgumentCaptor.forClass(Callback.class);
        mMockCall = Mockito.mock(Call.class);
        when(mMockWaTDAPI.uploadImage(any(RequestBody.class))).thenReturn(mMockCall);
        APIService.setInstance(mMockWaTDAPI);
    }

    @After
    public void tearDown() {
        Log.e("instance", "teardown");
        APIService.setInstance(null);
    }

    @Test
    public void testMainActivity_capture_success_response() throws InterruptedException {
        Response<ResponseModel> fakeSuccessResponse = Response.success(new ResponseModel());
        onView(withId(R.id.fab)).perform(click());
        Thread.sleep(1000);
        verify(mMockCall).enqueue(mCallCaptor.capture());
        mCallCaptor.getValue().onResponse(fakeSuccessResponse, null);
        onView(withText(R.string.done_upload_photo)).inRoot(withDecorView(not(mActivity.getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    @Test
    public void testMainActivity_capture_failed_response() throws InterruptedException {
        onView(withId(R.id.fab)).perform(click());
        Thread.sleep(1000);
        verify(mMockCall).enqueue(mCallCaptor.capture());
        mCallCaptor.getValue().onFailure(null);
        onView(withText(R.string.upload_failed)).inRoot(withDecorView(not(mActivity.getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

}
