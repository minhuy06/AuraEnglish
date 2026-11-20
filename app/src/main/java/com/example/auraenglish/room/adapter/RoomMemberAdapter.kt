package com.example.auraenglish.room.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ItemRoomMemberBinding
import com.example.auraenglish.practice.utils.gone
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.visible
import com.example.auraenglish.practice.utils.visibleIf
import com.example.auraenglish.room.data.RoomMemberUiModel
import com.example.auraenglish.room.data.RoomSlotItem

class RoomMemberAdapter(
    private val onEmptySlotClick: () -> Unit,
    private val onKickClick: (RoomMemberUiModel) -> Unit
) : ListAdapter<RoomSlotItem, RoomMemberAdapter.SlotViewHolder>(DiffCallback) {

    private object DiffCallback : DiffUtil.ItemCallback<RoomSlotItem>() {
        override fun areItemsTheSame(oldItem: RoomSlotItem, newItem: RoomSlotItem): Boolean =
            when {
                oldItem is RoomSlotItem.Filled && newItem is RoomSlotItem.Filled ->
                    oldItem.member.id == newItem.member.id
                oldItem is RoomSlotItem.Empty && newItem is RoomSlotItem.Empty ->
                    oldItem.slotIndex == newItem.slotIndex
                else -> false
            }

        override fun areContentsTheSame(oldItem: RoomSlotItem, newItem: RoomSlotItem): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val binding = ItemRoomMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SlotViewHolder(binding, onEmptySlotClick, onKickClick)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SlotViewHolder(
        private val binding: ItemRoomMemberBinding,
        private val onEmptySlotClick: () -> Unit,
        private val onKickClick: (RoomMemberUiModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RoomSlotItem) = with(binding) {
            when (item) {
                is RoomSlotItem.Filled -> bindFilled(item.member)
                is RoomSlotItem.Empty -> bindEmpty()
            }
        }

        private fun bindFilled(member: RoomMemberUiModel) = with(binding) {
            val context = root.context
            cardMemberFilled.visible()
            tvEmptyMemberSlot.gone()
            tvEmptyMemberSlot.setOnClickListener(null)

            tvMemberAvatar.text = member.initials
            tvMemberName.text = if (member.isCurrentUser) {
                "${member.name} ${context.getString(R.string.room_lobby_you_suffix)}"
            } else {
                member.name
            }

            if (member.isHost) {
                tvMemberRoleBadge.text = context.getString(R.string.room_lobby_host_badge)
                tvMemberRoleBadge.setBackgroundResource(R.drawable.bg_room_badge_host)
                tvMemberRoleBadge.setTextColor(ContextCompat.getColor(context, R.color.room_gold_text))
            } else {
                tvMemberRoleBadge.text = context.getString(R.string.room_lobby_ready_badge)
                tvMemberRoleBadge.setBackgroundResource(R.drawable.bg_room_badge_ready)
                tvMemberRoleBadge.setTextColor(ContextCompat.getColor(context, R.color.room_success_text))
            }

            val canKick = !member.isHost && !member.isCurrentUser
            btnKickMember.visibleIf(canKick)
            if (canKick) {
                btnKickMember.setOnClickDebounced { onKickClick(member) }
            } else {
                btnKickMember.setOnClickListener(null)
            }
        }

        private fun bindEmpty() = with(binding) {
            cardMemberFilled.gone()
            btnKickMember.setOnClickListener(null)
            tvEmptyMemberSlot.visible()
            tvEmptyMemberSlot.setOnClickDebounced { onEmptySlotClick() }
        }
    }
}
