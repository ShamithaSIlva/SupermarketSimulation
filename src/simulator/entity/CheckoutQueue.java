package simulator.entity;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JTextField;

import simulator.demos.Demo;
import simulator.util.RandomNumberGenerator;

/**
 * @author Shamitha This class acts as the real world Checkout of a Supermarket
 */
public class CheckoutQueue implements Runnable
{
	private Queue<Customer> customers;
	private RandomNumberGenerator generator;
	private String checkOutName;
	private int queueId;
	private double totalCustomerWaitingTime;
	private int totalCustomersProcessed;
	private int totalProductsProcessed;
	private int maximumProductCount = 200;
	private Lock sharedLockOnQueue = new ReentrantLock();
	private Condition conditionOnQueue;

	public CheckoutQueue()
	{
		customers = new LinkedList<>();
		generator = new RandomNumberGenerator();
	}

	public CheckoutQueue( int maximumProductCount )
	{
		this();
		this.maximumProductCount = maximumProductCount;
	}

	@Override
	public void run()
	{
		System.out.println( Thread.currentThread().getName() + " started..!!" );
		while ( true )
		{
			Customer customer = null;
			// System.out.println("Current Customers in Checkout Queue : "+Thread.currentThread().getName()+" ##" + customers.size());
            boolean isLockAcquired = sharedLockOnQueue.tryLock();
			if ( isLockAcquired )
			{
				try
				{
					if ( customers.size() > 0 )
					{
						customer = customers.poll();
						totalCustomersProcessed++;
						JTextField textField = Demo.getDemoInstance().getUi().getCheckOutAssociationMap().get( this.getQueueId() );
						textField.setText( Integer.toString( customers.size() ) );
					}
					else
					{
						try
						{
							setCondition( sharedLockOnQueue.newCondition() );
							getCondition().await();
						}
						catch ( InterruptedException e )
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				finally
				{
					sharedLockOnQueue.unlock();
				}
			}
			if ( customer != null )
			{
				int trolleyProductCount = customer.getTrolley().getProductCount();
				totalProductsProcessed += trolleyProductCount;
				for ( int i = 0; i < trolleyProductCount; i++ )
				{
					//System.out.println( "Customer is being processed.." );
					double tempTime = generator.getRandomDecimalNumberInRange( 0.5, 6 );
					long scanTime = ( long ) tempTime * 1000;
					DecimalFormat df = new DecimalFormat( "#.##" );
					// System.out.println( df.format( tempTime ) );
					totalCustomerWaitingTime += scanTime;
					try
					{
						Thread.sleep( scanTime );
					}
					catch ( InterruptedException e )
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				totalCustomerWaitingTime += customer.getWaitingTimeInQueue( System.currentTimeMillis() );
				System.out.println( "Customer ID : " + customer.getCustomerId() + " total waiting time in queue : " + customer.getWaitingTimeInQueue( System.currentTimeMillis() ) + " ## Product count : " + customer.getTrolley().getProductCount() );
			}
		}
	}	

	public Condition getCondition()
	{
		return conditionOnQueue;
	}

	public void setCondition( Condition condition )
	{
		this.conditionOnQueue = condition;
	}

	public Lock getSharedLock()
	{
		return sharedLockOnQueue;
	}

	public String getCheckOutName()
	{
		return checkOutName;
	}

	public void setCheckOutName( String checkOutName )
	{
		this.checkOutName = checkOutName;
	}

	public Queue<Customer> getCustomers()
	{
		return customers;
	}

	public void setCustomers( Queue<Customer> customers )
	{
		this.customers = customers;
	}

	public int getQueueId()
	{
		return queueId;
	}

	public void setQueueId( int queueId )
	{
		this.queueId = queueId;
	}

	public int getMaximumProductCount()
	{
		return maximumProductCount;
	}

	public int getTotalCustomersProcessed()
	{
		return totalCustomersProcessed;
	}

	public double getTotalCustomerWaitingTime()
	{
		return totalCustomerWaitingTime;
	}

	public int getTotalProductsProcessed()
	{
		return totalProductsProcessed;
	}
}
