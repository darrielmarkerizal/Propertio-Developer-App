package com.propertio.developer.project.list

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.propertio.developer.api.developer.projectmanagement.ProjectDetail
import com.propertio.developer.databinding.CardFileThumbnailBinding
import java.util.Locale

class FileThumbnailAdapter(
    private val projectDocuments: List<ProjectDetail.ProjectDeveloper.ProjectDocument>,
    private val onClickFile: (ProjectDetail.ProjectDeveloper.ProjectDocument) -> Unit
) : RecyclerView.Adapter<FileThumbnailAdapter.FileThumbnailViewHolder>() {
    inner class FileThumbnailViewHolder(
        private val binding: CardFileThumbnailBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(projectDocument: ProjectDetail.ProjectDeveloper.ProjectDocument) {
            with(binding) {
                Log.d("FileThumbnailAdapter", "bind: $projectDocument")

                textViewFilename.text = projectDocument.filename?.split("/")?.last() ?: "Dokumen Proyek"

                cardFileThumbnail.setOnClickListener {
                    onClickFile(projectDocument)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileThumbnailViewHolder {
        val binding = CardFileThumbnailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return FileThumbnailViewHolder(binding)
    }

    override fun getItemCount(): Int = projectDocuments.size

    override fun onBindViewHolder(holder: FileThumbnailViewHolder, position: Int) {
        holder.bind(projectDocuments[position])
    }
}
