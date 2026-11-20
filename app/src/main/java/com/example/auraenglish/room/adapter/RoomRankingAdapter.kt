package com.example.auraenglish.room.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ItemRoomRankRowBinding
import com.example.auraenglish.room.data.RoomRankEntryUiModel

class RoomRankingAdapter :
    ListAdapter<RoomRankEntryUiModel, RoomRankingAdapter.RankViewHolder>(DiffCallback) {

    private object DiffCallback : DiffUtil.ItemCallback<RoomRankEntryUiModel>() {
        override fun areItemsTheSame(
            oldItem: RoomRankEntryUiModel,
            newItem: RoomRankEntryUiModel
        ): Boolean = oldItem.member.id == newItem.member.id

        override fun areContentsTheSame(
            oldItem: RoomRankEntryUiModel,
            newItem: RoomRankEntryUiModel
        ): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankViewHolder {
        val binding = ItemRoomRankRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RankViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RankViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RankViewHolder(
        private val binding: ItemRoomRankRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: RoomRankEntryUiModel) = with(binding) {
            val context = root.context
            val member = entry.member

            tvRankPosition.text = "#${entry.rank}"
            tvRankAvatar.text = member.initials
            tvRankMemberName.text = if (member.isCurrentUser) {
                "${member.name} ${context.getString(R.string.room_lobby_you_suffix)}"
            } else {
                member.name
            }
            tvRankDetailMeta.text = context.getString(
                R.string.room_ranking_detail_fmt,
                entry.correctCount,
                entry.totalQuestions,
                entry.formattedTime
            )
            tvRankPoints.text = context.getString(R.string.room_ranking_score_fmt, entry.score)
            cardRoomRankRow.setBackgroundResource(
                if (member.isCurrentUser) R.drawable.bg_room_rank_me else R.drawable.bg_room_card
            )
        }
    }
}
