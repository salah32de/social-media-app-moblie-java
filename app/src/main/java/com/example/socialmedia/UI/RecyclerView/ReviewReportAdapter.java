package com.example.socialmedia.UI.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialmedia.Control.PostManager;
import com.example.socialmedia.Control.SharedPreferencesManager;
import com.example.socialmedia.Control.UserManager;
import com.example.socialmedia.Data.Firebase.RealtimeDatabase.PostRepository;
import com.example.socialmedia.Data.Firebase.RealtimeDatabase.UserRepository;
import com.example.socialmedia.Model.Post;
import com.example.socialmedia.Model.Report;
import com.example.socialmedia.Model.User;
import com.example.socialmedia.R;
import com.example.socialmedia.UI.Activity.Profile;
import com.example.socialmedia.UI.Fragment.CommentFragment;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ReviewReportAdapter extends RecyclerView.Adapter<ReviewReportAdapter.ReviewReportViewHolder> {

    private List<Report> reportList;
    private Activity context;

    public ReviewReportAdapter(Activity context, List<Report> reportList) {
        this.reportList = reportList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ReviewReportViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_reports, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ReviewReportViewHolder holder, int position) {
        holder.bind(context, reportList.get(position));
    }

    @Override
    public int getItemCount() {
        if (reportList != null)
            return reportList.size();
        return 0;
    }

    public static class ReviewReportViewHolder extends RecyclerView.ViewHolder {
        private TextView reportType, reportCategory, reportFrom, reportTime, reportTarget, showItemReported;
        private Button btnIgnore, btnBlock;
        private View postLayout;


        private ImageView imageProfile, menuSitting, imagePost;
        private  PlayerView videoPost;
        private   TextView nameUser, datePublish, textPost;
        private  TextView like, comment, save;
        private   SimpleExoPlayer player;

        public ReviewReportViewHolder(@NonNull View itemView) {
            super(itemView);
            reportType = itemView.findViewById(R.id.report_item_type);
            reportCategory = itemView.findViewById(R.id.report_category);
            reportFrom = itemView.findViewById(R.id.report_from);
            reportTime = itemView.findViewById(R.id.report_time);
            reportTarget = itemView.findViewById(R.id.report_target);
            btnIgnore = itemView.findViewById(R.id.btn_ignore);
            btnBlock = itemView.findViewById(R.id.btn_block);
            showItemReported = itemView.findViewById(R.id.showItemReported);
            postLayout = itemView.findViewById(R.id.postLayout);

            imageProfile = itemView.findViewById(R.id.userImage);
            menuSitting = itemView.findViewById(R.id.menuSetting);
            imagePost = itemView.findViewById(R.id.imagePost);
            videoPost = itemView.findViewById(R.id.videoPost);
            nameUser = itemView.findViewById(R.id.nameUser);
            datePublish = itemView.findViewById(R.id.date);
            textPost = itemView.findViewById(R.id.textPost);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            player = new SimpleExoPlayer.Builder(itemView.getContext()).build();
            videoPost.setPlayer(player);

        }

        public void bind(Context context, Report report) {

            postLayout.setVisibility(View.GONE);


            reportType.setText("report of: "+String.valueOf(report.getReportItem()));
            reportCategory.setText("type rerpot: "+String.valueOf(report.getReportCategory()));
            reportFrom.setText("from: "+report.getReporterUserId());
            reportTime.setText("datetime: "+convertMillisToDateTime(report.getReportTimestamp()));
            reportTarget.setText("reason write the report: "+report.getReportContent());


            if (report.getReportItem().equals(Report.ReportItem.POST)) {
                showItemReported.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PostManager postManager = new PostManager();
                        postManager.GetPostById(report.getReportedItemId(), new PostRepository.GetPostByIdCallback() {
                            @Override
                            public void getPostByIdSuccess(Post post) {
                                postLayout.setVisibility(View.VISIBLE);
                                if (!post.getUserCreatePost().getPhotoProfile().isEmpty())
                                    Glide.with(context).load(post.getUserCreatePost().getPhotoProfile()).circleCrop().placeholder(R.drawable.wait_download).into(imageProfile);
                                if (post.getLink().contains(".mp4")) {
                                    imagePost.setVisibility(View.GONE);
                                    videoPost.setVisibility(View.VISIBLE);


                                    MediaItem mediaItem = MediaItem.fromUri(post.getLink());
                                    player.setMediaItem(mediaItem);
                                    player.prepare();


                                } else if (post.getLink().contains(".jpg") || post.getLink().contains(".png")) {
                                    videoPost.setVisibility(View.GONE);
                                    imagePost.setVisibility(View.VISIBLE);
                                    Glide.with(context).load(post.getLink()).placeholder(R.drawable.edittext_background).into(imagePost);
                                } else {
                                    imagePost.setVisibility(View.GONE);
                                    videoPost.setVisibility(View.GONE);
                                }

                                textPost.setText(post.getText());
                                nameUser.setText(post.getUserCreatePost().getName());
                                datePublish.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(post.getDate()));
                                //button
                                like.setText(post.getLikes() + "");
                                comment.setText(post.getNumComments() + "");


                                comment.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        View fragment = ((AppCompatActivity) context).findViewById(R.id.CommentFragment);
                                        fragment.setVisibility(View.VISIBLE);
                                        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                                        CommentFragment commentFragment = new CommentFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("post", post);
                                        commentFragment.setArguments(bundle);
                                        transaction.replace(R.id.CommentFragment, commentFragment);
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                    }
                                });

                            }

                            @Override
                            public void getPostByIdFailure(Exception e) {
                                Toast.makeText(context, "get reported item is failed ", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            } else {
                showItemReported.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        UserManager userManager = new UserManager();
                        userManager.getUserById(report.getReporterUserId(), new UserRepository.UserCallBack<User>() {
                            @Override
                            public void onSuccess(User value) {
                                Intent intent = new Intent(context, Profile.class);
                                intent.putExtra(SharedPreferencesManager.USER_KEY, value);
                                (context).startActivity(intent);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(context, "get user reporter is failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }


        }

    }

    public static String convertMillisToDateTime(long millis) {
        Date date = new Date(millis);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        formatter.setTimeZone(TimeZone.getDefault()); // imeZone.getTimeZone("GMT")

        return formatter.format(date);
    }


}
