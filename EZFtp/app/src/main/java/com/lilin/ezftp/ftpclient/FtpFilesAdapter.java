package com.lilin.ezftp.ftpclient;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.lilin.ezftp.R;
import com.lilin.ezftp.databinding.ItemFileBinding;
import com.lilincpp.github.libezftp.EZFtpFile;

import java.util.List;

/**
 * 文件列表适配器
 *
 * ftp server file list adapter
 *
 * @author lilin
 */
public class FtpFilesAdapter extends RecyclerView.Adapter<FtpFilesAdapter.ViewHolder> {

    private static final String TAG = "FtpFilesAdapter";

    private List<EZFtpFile> ftpFiles;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onClick(EZFtpFile ftpFile);
    }

    public FtpFilesAdapter() {
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setFtpFiles(List<EZFtpFile> ftpFiles) {
        this.ftpFiles = ftpFiles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemFileBinding binding
                = DataBindingUtil.inflate(
                inflater,
                R.layout.item_file,
                parent,
                false
        );
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final EZFtpFile ftpFile = ftpFiles.get(position);
        Log.e(TAG, "onBindViewHolder: ftpFile = " + ftpFile.toString());
        final String typeFile = holder.itemView.getContext().getString(R.string.file);
        final String typeDir = holder.itemView.getContext().getString(R.string.dir);
        final String typeLink = holder.itemView.getContext().getString(R.string.link);
        final String fileType =
                ftpFile.getType() == EZFtpFile.TYPE_FILE ? typeFile :
                        (ftpFile.getType() == EZFtpFile.TYPE_DIRECTORY ? typeDir : typeLink);

        if (ftpFile.getType() == EZFtpFile.TYPE_FILE) {
            holder.binding.tvSize.setVisibility(View.VISIBLE);
        } else {
            holder.binding.tvSize.setVisibility(View.GONE);
        }

        holder.binding.setFileName(ftpFile.getName());
        holder.binding.setType(fileType);
        holder.binding.setFileSize(ConvertUtils.byte2FitMemorySize(ftpFile.getSize(), 1));
        holder.binding.setFileDate(TimeUtils.date2String(ftpFile.getModifiedDate()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(ftpFile);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return ftpFiles == null ? 0 : ftpFiles.size();
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        ItemFileBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.findBinding(itemView);
        }
    }
}
