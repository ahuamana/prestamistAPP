package com.paparazziapps.pretamistapp.presentation.profile.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentProfileBinding
import com.paparazziapps.pretamistapp.presentation.profile.viewmodels.ProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : Fragment() {

    private val viewModel by viewModel<ProfileViewModel>()

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            tvEmail.text = viewModel.getEmail()
            tvJoinedDate.text = viewModel.getCreationDate()
            tvFilial.text = viewModel.getBranchName()
            tvProfileStatus.text = viewModel.getIsActive()
            tvRole.text = viewModel.getRole()
            tvFullName.text = viewModel.getFullName()

            btnLogout.setOnClickListener {
                viewModel.logout(requireContext())
            }
        }



    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}