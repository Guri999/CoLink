package com.seven.colink.ui.evaluation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.seven.colink.databinding.FragmentEvaluationProjectBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EvaluationProjectFragment : Fragment() {
    private var _binding: FragmentEvaluationProjectBinding? = null
    private val binding get() = _binding!!
    private val evalProjectViewModel: EvaluationProjectViewModel by viewModels()
    private var projectUserList = mutableListOf<EvaluationData.EvalProject>()

    companion object {
        fun newInstanceProject(list: List<EvaluationData.EvalProject>)
        : EvaluationProjectFragment {
            val fragment = EvaluationProjectFragment()
            val args = Bundle()
            args.putParcelableArrayList("projectUserList", ArrayList(list))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEvaluationProjectBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setObserve()
    }

    private fun initView(){
        Log.d("Evaluation", "EvaluationProjectFragment")
        arguments?.let {
            val projectUserList: ArrayList<EvaluationData.EvalProject> =
                it.getParcelableArrayList("projectUserList") ?: arrayListOf()
            Log.d("Evaluation", "projectUserlist = ${projectUserList}")
        }

    }

    private fun setObserve() {
        evalProjectViewModel.evalProjectData.observe(viewLifecycleOwner){

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}