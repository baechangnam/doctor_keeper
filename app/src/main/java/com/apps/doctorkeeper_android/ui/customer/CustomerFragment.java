package com.apps.doctorkeeper_android.ui.customer;

import static com.apps.doctorkeeper_android.constants.CommonValue.DIAGNOSIS_LIST;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apps.doctorkeeper_android.R;
import com.apps.doctorkeeper_android.api.AppApi;
import com.apps.doctorkeeper_android.base.BaseFragment;
import com.apps.doctorkeeper_android.data.consult.CommonCode;
import com.apps.doctorkeeper_android.data.consult.CommonCodeResponse;
import com.apps.doctorkeeper_android.data.customer.model.CustomerModel;
import com.apps.doctorkeeper_android.databinding.FragmentCustomerBinding;
import com.apps.doctorkeeper_android.network.RetrofitAdapter;
import com.apps.doctorkeeper_android.ui.consult.ItemClick;
import com.apps.doctorkeeper_android.ui.customer.adapter.CustomerAdapter;
import com.apps.doctorkeeper_android.ui.login.IPSettingDialog;
import com.apps.doctorkeeper_android.ui.main.HomeFragment;
import com.apps.doctorkeeper_android.util.CommonUtils;
import com.apps.doctorkeeper_android.util.KoreanMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CustomerFragment extends BaseFragment implements MyDialogListener , ItemClick {

    FragmentCustomerBinding binding;
    CustomerViewModel customerViewModel;
    View rootView;
    ArrayList<CustomerModel> patientList = new ArrayList<>();
    CustomerAdapter mAdapter;

    String charNO = "";
    int page = 1;
    int totalPage = 0;

    String rows ="20";
    boolean isLastLoading = false; //마지막페이지일때 처리
    boolean isLoading = false; //리스트 로딩중일때 처리

    boolean isSearch = false; //검색어 입력중일때 처리

    boolean isSearching = false; //검색결과 검색완료시

    String tabTag="recent";
    String typeCode="";

    boolean[] checkItems = {}; //진료분류 체크값
    String[] cdNames ={}; //진료분류 스트링값
    List<String> names = new ArrayList(); //진료분류 출력용
    List<String> codes = new ArrayList(); //진료분류 코드


    @Override
    public void OnCloseDialog() {
        if(tabTag.equals("recent")){
            page = 1;
            customerViewModel.getPatientList("1", "1",page + "",rows);
        }else if(tabTag.equals("all")){
            page = 1;
            customerViewModel.getPatientList("", "1",page + "",rows);

        }else if(tabTag.equals("type")){
            customerViewModel.getPatientListCustInfoSearch(typeCode);
        }
    }

    public static CustomerFragment newInstance(Bundle args) {
        CustomerFragment fragment = new CustomerFragment();
        if (args != null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new CustomerViewModel(getActivity().getApplication(),
                    getActivity());
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCustomerBinding.inflate(getLayoutInflater());
        rootView = binding.getRoot();

        getCommonCode("039");


        customerViewModel = new ViewModelProvider(this, factory).get(CustomerViewModel.class);
        setListCommon();
        initViewModelObserving();
        initView();
        customerViewModel.getPatientList("1", "1",page + "",rows);

        return rootView;
    }

    private void showDiagnosisType(TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(cdNames, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                dialog.dismiss();
                String custInfo="";


                custInfo = codes.get(pos);
                isSearching=false;
                tabTag="type";
                page = 1;
                //binding.etSearch.setText("");
                binding.etSearch.setText("["+cdNames[pos]+"] 에서 검색");
                binding.tvType.setBackgroundColor(mActivity.getColor(R.color.gray));
                binding.tvType.setTextColor(mActivity.getColor(R.color.white));

                binding.tvAllCs.setBackgroundColor(mActivity.getColor(R.color.white));
                binding.tvAllCs.setTextColor(mActivity.getColor(R.color.gray));

                binding.tvRecentCs.setBackgroundColor(mActivity.getColor(R.color.white));
                binding.tvRecentCs.setTextColor(mActivity.getColor(R.color.gray));

                typeCode= custInfo;
                customerViewModel.getPatientListCustInfoSearch(custInfo);

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void initView() {
        binding.btAddUser.setOnClickListener(view -> {
            CustomerRegDialog customerRegDialog = CustomerRegDialog.getInstance(this);
            customerRegDialog.show(getFragmentManager(), IPSettingDialog.TAG_EVENT_DIALOG);
        });

        binding.etSearch.addTextChangedListener(textWatcher);
        binding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeypad(getActivity(), binding.etSearch);
                    customerViewModel.getPatientListSearch(binding.etSearch.getText().toString());
                    isSearching=true;

                }
                return false;
            }
        });

        binding.btRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tabTag.equals("recent")){
                    page = 1;
                    customerViewModel.getPatientList("1", "1",page + "",rows);
                }else if(tabTag.equals("all")){
                        page = 1;
                        customerViewModel.getPatientList("", "1",page + "",rows);

                }else if(tabTag.equals("type")){
                    customerViewModel.getPatientListCustInfoSearch(typeCode);
                }
            }
        });

        binding.btDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.hideBottomMenu();
                HomeFragment fragment = HomeFragment.newInstance(null);
                mActivity.startFragment(fragment);
            }
        });

        binding.tvRecentCs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSearching=false;
                tabTag="recent";
                page = 1;
                binding.etSearch.setText("");
                binding.tvRecentCs.setBackgroundColor(mActivity.getColor(R.color.gray));
                binding.tvRecentCs.setTextColor(mActivity.getColor(R.color.white));

                binding.tvAllCs.setBackgroundColor(mActivity.getColor(R.color.white));
                binding.tvAllCs.setTextColor(mActivity.getColor(R.color.gray));

                binding.tvType.setBackgroundColor(mActivity.getColor(R.color.white));
                binding.tvType.setTextColor(mActivity.getColor(R.color.gray));

                customerViewModel.getPatientList("1", "1",page + "",rows);
            }
        });

        binding.tvType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  showDiagnosisType(binding.tvAllCs);

            }
        });

        binding.tvAllCs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSearching=false;
                tabTag="all";
                page = 1;
                binding.etSearch.setText("");
                binding.tvAllCs.setBackgroundColor(mActivity.getColor(R.color.gray));
                binding.tvAllCs.setTextColor(mActivity.getColor(R.color.white));

                binding.tvRecentCs.setBackgroundColor(mActivity.getColor(R.color.white));
                binding.tvRecentCs.setTextColor(mActivity.getColor(R.color.gray));

                binding.tvType.setBackgroundColor(mActivity.getColor(R.color.white));
                binding.tvType.setTextColor(mActivity.getColor(R.color.gray));

                customerViewModel.getPatientList("", "1",page + "",rows);

            }
        });

    }

    private void initViewModelObserving() {
        customerViewModel.getCustomerLiveData().observe(mActivity, response -> {

            if (response == null) {
                CommonUtils.showSnackBar(binding.tvRecentCs, "응답이 없습니다. IP주소나 인터넷 연결상태를 확인해 주세요");
            } else {
                if (Integer.valueOf(response.getPage()) == totalPage) {
                    isLastLoading = true;
                }
                isLoading = false;

                Log.d("myLog", "size " + response.getRESULT().size());

                if (page == 1) {
                    patientList.clear();
                    totalPage = Integer.valueOf(response.getTotal());
                    patientList.addAll(response.getRESULT());
                    tempList.addAll(patientList);
                    setListCommon();
                } else {
                    patientList.addAll(response.getRESULT());
                    tempList.addAll(patientList);
                    mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
                }

            }
        });
    }


    private void setListCommon() {
        binding.rvCommon.setNestedScrollingEnabled(true);

        LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);

        binding.rvCommon.setLayoutManager(mLayoutManager);
        mAdapter = new CustomerAdapter(mActivity,
                patientList,this);// 리스트
        mAdapter.setHasStableIds(true);
        binding.rvCommon.setAdapter(mAdapter);

        binding.rvCommon.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                isSearch = false;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();

                if (!isLoading && !isSearch && !isSearching) {
                    if (lastVisible >= totalItemCount - 1) {
                        if (!isLastLoading) {
                            isLoading = true;
                            page++;
                            customerViewModel.getPatientList("1", "1",page + "",rows);

                            Log.d("myLog", "page " + page);
                        }

                    }
                }


            }
        });

    }

    ArrayList<CustomerModel> searchList = new ArrayList<>();
    ArrayList<CustomerModel> tempList = new ArrayList<>();
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            isSearch = true;
            isSearching=false;
            if (charSequence.length() != 0) {
                searchList.clear();

                String searchKeyword = binding.etSearch.getText().toString();
                for (CustomerModel cs : tempList) {
                    if (KoreanMatcher.matchKoreanAndConsonant(cs.getCustNm(), searchKeyword)){
                        searchList.add(cs);
                    }else{
                        if (cs.getCustNm().toLowerCase(Locale.getDefault()).contains(searchKeyword.toLowerCase(Locale.getDefault()))) {
                            searchList.add(cs);
                        }
                    }

                }

                Log.d("myLog", "searchList " + searchList.size());

                if(searchList.size()>0){
                    patientList.clear();
                    patientList.addAll(searchList);
                }else{
                    patientList.clear();
                }

            } else {
                patientList.clear();
                patientList.addAll(tempList);
            }

            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    //고객이름 클릭시
    @Override
    public void itemClick(String code, String idx) {
        Bundle bundle = new Bundle();
        bundle.putString("code", code);
        bundle.putString("charNo", idx);
        CustomerImageFragment customerImageFragment = CustomerImageFragment.newInstance(bundle);
        mActivity.hideBottomMenu();
        mActivity.addFragment(customerImageFragment);

      //  customerViewModel.getPatientImageList(code);
    }


    public void getCommonCode(String cdGrpId) {
        AppApi appApi = RetrofitAdapter.getClient(requireActivity()).create(AppApi.class);
        appApi.getCommonCode(cdGrpId)
                .enqueue(new Callback<CommonCodeResponse>() {
                    @Override
                    public void onResponse(Call<CommonCodeResponse> call, Response<CommonCodeResponse> response) {
                        ArrayList<CommonCode> commonCodes = response.body().getRESULT();

                        checkItems = new boolean[commonCodes.size()];
                        cdNames = new String[commonCodes.size()];
                        int i=0;
                        for(CommonCode cCode : commonCodes){
                            checkItems[i] = false;
                            cdNames[i] = cCode.getCdNm();
                            codes.add(cCode.getCdId());
                            i++;
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonCodeResponse> call, Throwable t) {


                    }
                });

    }
}
