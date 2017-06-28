package com.rockchips.android.leanbacklauncher.settings;

import android.content.Context;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;
import com.rockchips.android.leanbacklauncher.recommendations.SwitchingRecommendationsClient;
import com.rockchips.android.leanbacklauncher.tvrecommendations.IRecommendationsService;

import java.util.List;

public class BlacklistManager {
    private Context mContext;

    private static abstract class AsyncRecommendationsClient extends SwitchingRecommendationsClient {

        private class Task extends AsyncTask<IRecommendationsService, Void, Void> {
            private Task() {
            }

            protected Void doInBackground(IRecommendationsService... params) {
                try {
                    AsyncRecommendationsClient.this.callServiceInBackground(params[0]);
                } catch (RemoteException e) {
                    Log.e("BlacklistManager", "Call to recommendation service failed", e);
                } finally {
                    AsyncRecommendationsClient.this.disconnect();
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                AsyncRecommendationsClient.this.onPostExecute();
            }
        }

        protected abstract void callServiceInBackground(IRecommendationsService iRecommendationsService) throws RemoteException;

        public AsyncRecommendationsClient(Context context) {
            super(context);
        }

        protected void onConnected(IRecommendationsService service) {
            new Task().execute(new IRecommendationsService[]{service});
        }

        protected void onDisconnected() {
        }

        protected void onPostExecute() {
        }
    }

    public interface LoadRecommendationPackagesCallback {
        void onRecommendationPackagesLoaded(String[] strArr, String[] strArr2);
    }

    private static class LoadRecommendationPackagesTask extends AsyncRecommendationsClient {
        private String[] mBlacklistedPackages;
        private final LoadRecommendationPackagesCallback mCallback;
        private String[] mRecommendationPackages;

        public LoadRecommendationPackagesTask(Context context, LoadRecommendationPackagesCallback callback) {
            super(context);
            this.mCallback = callback;
        }

        protected void callServiceInBackground(IRecommendationsService service) throws RemoteException {
            try {
                this.mRecommendationPackages = service.getRecommendationsPackages();
                this.mBlacklistedPackages = service.getBlacklistedPackages();
            } catch (RemoteException e) {
                this.mRecommendationPackages = new String[0];
                this.mBlacklistedPackages = new String[0];
            }
        }

        protected void onPostExecute() {
            this.mCallback.onRecommendationPackagesLoaded(this.mRecommendationPackages, this.mBlacklistedPackages);
        }
    }

    private static class SaveBlacklistTask extends AsyncRecommendationsClient {
        private String[] mBlacklistedPackages;

        public SaveBlacklistTask(Context context, List<String> packages) {
            super(context);
            this.mBlacklistedPackages = (String[]) packages.toArray(new String[packages.size()]);
        }

        protected void callServiceInBackground(IRecommendationsService service) throws RemoteException {
            service.setBlacklistedPackages(this.mBlacklistedPackages);
        }
    }

    public BlacklistManager(Context context) {
        this.mContext = context;
    }

    public void saveEntityBlacklist(List<String> keys) {
        new SaveBlacklistTask(this.mContext, keys).connect();
    }

    public void loadRecommendationPackages(LoadRecommendationPackagesCallback callback) {
        new LoadRecommendationPackagesTask(this.mContext, callback).connect();
    }
}
